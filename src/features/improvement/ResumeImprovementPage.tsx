import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api } from '@/services/api';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { CheckCircle2, ArrowRight, Copy, Sparkles, Loader2 } from 'lucide-react';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { useJobPolling } from '@/hooks/useJobPolling';
import toast from 'react-hot-toast';

export const ResumeImprovementPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const analysisId = searchParams.get('analysisId');

  const [improvement, setImprovement] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [jobId, setJobId] = useState<string | null>(null);

  const { data: jobData, error: pollError, isPolling } = useJobPolling<any>(jobId);

  // Initial fetch check
  useEffect(() => {
    if (!analysisId) {
      setError('No analysis ID provided.');
      setLoading(false);
      return;
    }
    fetchImprovement();
  }, [analysisId]);

  // Polling completion
  useEffect(() => {
    if (jobData?.status === 'COMPLETED') {
      fetchImprovement();
      setJobId(null);
    }
  }, [jobData]);

  const fetchImprovement = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/improvement/${analysisId}`);
      setImprovement(response.data);
      setError(null);
    } catch (err: any) {
      if (err.response?.status === 404) {
        // It's okay, we haven't generated one yet
        setImprovement(null);
      } else {
        setError(err.response?.data?.message || 'Failed to fetch improvements.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleGenerate = async () => {
    if (!analysisId) return;
    try {
      setError(null);
      const response = await api.post(`/improvement/${analysisId}`);
      if (response.status === 202 && response.data.jobId) {
        setJobId(response.data.jobId);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to start generation.');
    }
  };

  const handleCopyAll = () => {
    if (!improvement || !improvement.improvements) return;
    const text = improvement.improvements.map((b: any) => `- ${b.improvedBullet}`).join('\n\n');
    navigator.clipboard.writeText(text);
    toast.success('All improved bullets copied to clipboard!');
  };

  if (loading && !isPolling) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (error || pollError) {
    return (
      <div className="p-8 max-w-3xl mx-auto mt-8">
        <Alert variant="destructive">
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>{error || pollError}</AlertDescription>
        </Alert>
      </div>
    );
  }

  // State: Needs generation
  if (!improvement && !isPolling) {
    return (
      <div className="max-w-3xl mx-auto mt-12 text-center space-y-6">
        <Sparkles className="h-16 w-16 mx-auto text-primary opacity-80" />
        <h2 className="text-3xl font-bold">Resume Improvement Engine</h2>
        <p className="text-muted-foreground text-lg">
          Let our AI analyze your original resume bullets and rewrite them to highlight your impact, align with the job description, and pass ATS filters.
        </p>
        <Button size="lg" onClick={handleGenerate} className="w-full sm:w-auto">
          Generate Improvements
        </Button>
      </div>
    );
  }

  // State: Generating (Polling)
  if (isPolling) {
    return (
      <div className="max-w-2xl mx-auto mt-12 space-y-8">
        <div className="text-center space-y-4">
          <Sparkles className="h-12 w-12 mx-auto text-primary animate-pulse" />
          <h2 className="text-2xl font-semibold">Rewriting your resume...</h2>
          <p className="text-muted-foreground">
            Applying industry best practices to maximize impact.
          </p>
        </div>
        <Progress value={jobData?.progress || 15} className="w-full h-3" />
        <p className="text-center text-sm font-medium text-slate-600">
          {jobData?.status === 'PROCESSING' ? 'Drafting improvements...' : 'Starting...'}
        </p>
      </div>
    );
  }

  // State: Completed
  return (
    <div className="max-w-5xl mx-auto space-y-8 mt-8 pb-12">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h2 className="text-3xl font-bold tracking-tight flex items-center gap-2">
            <Sparkles className="h-6 w-6 text-primary" />
            Resume Improvements
          </h2>
          <p className="text-muted-foreground mt-2">
            Review your AI-enhanced bullets side-by-side with your originals.
          </p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" onClick={handleCopyAll} className="gap-2">
            <Copy className="h-4 w-4" /> Copy All
          </Button>
        </div>
      </div>

      <Card className="bg-primary/5 border-primary/20">
        <CardContent className="p-6">
          <h3 className="font-semibold text-lg mb-2">Summary of Changes</h3>
          <p className="text-slate-700 leading-relaxed">{improvement.summary}</p>
        </CardContent>
      </Card>

      <div className="space-y-6">
        {improvement.improvements?.map((item: any, idx: number) => (
          <Card key={idx} className="overflow-hidden">
            <div className="flex flex-col lg:flex-row">
              {/* Original */}
              <div className="flex-1 p-6 bg-slate-50 lg:border-r border-b lg:border-b-0">
                <div className="text-sm font-semibold text-slate-500 mb-3 uppercase tracking-wider">
                  Original
                </div>
                <p className="text-slate-700">{item.originalBullet}</p>
              </div>
              
              {/* Arrow Indicator (Desktop) */}
              <div className="hidden lg:flex items-center justify-center -mx-4 z-10">
                <div className="bg-white rounded-full p-2 border shadow-sm">
                  <ArrowRight className="h-5 w-5 text-slate-400" />
                </div>
              </div>

              {/* Improved */}
              <div className="flex-1 p-6 bg-white relative">
                <div className="flex justify-between items-start mb-3">
                  <div className="text-sm font-semibold text-primary uppercase tracking-wider flex items-center gap-2">
                    <CheckCircle2 className="h-4 w-4" /> Improved
                  </div>
                  <div className="flex gap-2">
                    <Badge variant="outline" className="text-xs">
                      {item.category}
                    </Badge>
                  </div>
                </div>
                {/* Highlight formatting can be added here by parsing additions, but for now we display full text */}
                <p className="text-slate-900 font-medium leading-relaxed">
                  {item.improvedBullet}
                </p>
                <div className="mt-4 p-3 bg-blue-50/50 rounded text-sm text-slate-600 border border-blue-100">
                  <span className="font-semibold text-slate-700">Why:</span> {item.reason}
                </div>
                <Button 
                  variant="ghost" 
                  size="sm" 
                  className="absolute top-4 right-4 opacity-50 hover:opacity-100"
                  onClick={() => {
                    navigator.clipboard.writeText(item.improvedBullet);
                    toast.success('Copied to clipboard');
                  }}
                >
                  <Copy className="h-4 w-4" />
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
};
