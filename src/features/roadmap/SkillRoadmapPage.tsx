import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api } from '@/services/api';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { BookOpen, Compass, Code, ExternalLink, Calendar, GitCommit, Sparkles, Loader2, ArrowRight } from 'lucide-react';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { useJobPolling } from '@/hooks/useJobPolling';

export const SkillRoadmapPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const analysisId = searchParams.get('analysisId');

  const [roadmap, setRoadmap] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [jobId, setJobId] = useState<string | null>(null);

  const { data: jobData, error: pollError, isPolling } = useJobPolling<any>(jobId);

  useEffect(() => {
    if (!analysisId) {
      setError('No analysis ID provided.');
      setLoading(false);
      return;
    }
    fetchRoadmap();
  }, [analysisId]);

  useEffect(() => {
    if (jobData?.status === 'COMPLETED') {
      fetchRoadmap();
      setJobId(null);
    }
  }, [jobData]);

  const fetchRoadmap = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/roadmap/${analysisId}`);
      setRoadmap(response.data);
      setError(null);
    } catch (err: any) {
      if (err.response?.status === 404) {
        setRoadmap(null);
      } else {
        setError(err.response?.data?.message || 'Failed to fetch roadmap.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleGenerate = async () => {
    if (!analysisId) return;
    try {
      setError(null);
      const response = await api.post(`/roadmap/${analysisId}`);
      if (response.status === 202 && response.data.jobId) {
        setJobId(response.data.jobId);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to start generation.');
    }
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

  // Needs generation
  if (!roadmap && !isPolling) {
    return (
      <div className="max-w-3xl mx-auto mt-12 text-center space-y-6">
        <Compass className="h-16 w-16 mx-auto text-primary opacity-80" />
        <h2 className="text-3xl font-bold">Skill Gap Roadmap</h2>
        <p className="text-muted-foreground text-lg">
          Generate a personalized learning path to bridge the gap between your current skills and the target job description.
        </p>
        <Button size="lg" onClick={handleGenerate} className="w-full sm:w-auto">
          Generate Roadmap
        </Button>
      </div>
    );
  }

  // Polling
  if (isPolling) {
    return (
      <div className="max-w-2xl mx-auto mt-12 space-y-8">
        <div className="text-center space-y-4">
          <Compass className="h-12 w-12 mx-auto text-primary animate-spin-slow" />
          <h2 className="text-2xl font-semibold">Plotting your course...</h2>
          <p className="text-muted-foreground">
            Analyzing dependencies and prioritizing skills for maximum impact.
          </p>
        </div>
        <Progress value={jobData?.progress || 15} className="w-full h-3" />
        <p className="text-center text-sm font-medium text-slate-600">
          {jobData?.status === 'PROCESSING' ? 'Building learning phases...' : 'Starting...'}
        </p>
      </div>
    );
  }

  // Completed
  return (
    <div className="max-w-5xl mx-auto space-y-8 mt-8 pb-12">
      <div>
        <h2 className="text-3xl font-bold tracking-tight flex items-center gap-2">
          <Compass className="h-8 w-8 text-primary" />
          Your Personalized Roadmap
        </h2>
        <p className="text-muted-foreground mt-2">
          A step-by-step guide to acquiring the skills needed for the job.
        </p>
      </div>

      <div className="space-y-8">
        {roadmap.phases?.map((phase: any, phaseIdx: number) => (
          <Card key={phaseIdx} className="overflow-hidden border-l-4 border-l-primary relative">
            <CardHeader className="bg-slate-50 border-b pb-4">
              <div className="flex justify-between items-start">
                <div>
                  <Badge variant="secondary" className="mb-2 text-primary bg-primary/10">Phase {phaseIdx + 1}</Badge>
                  <CardTitle className="text-xl">{phase.phaseName}</CardTitle>
                  <CardDescription className="flex items-center gap-2 mt-1 font-medium">
                    <Calendar className="h-4 w-4" /> Expected Duration: {phase.expectedDurationWeeks} Weeks
                  </CardDescription>
                </div>
                {phase.aiMotivationalGuidance && (
                  <div className="hidden md:flex bg-blue-50 text-blue-800 text-sm p-3 rounded-lg border border-blue-100 max-w-sm gap-2">
                    <Sparkles className="h-4 w-4 shrink-0 text-blue-600 mt-0.5" />
                    <span>{phase.aiMotivationalGuidance}</span>
                  </div>
                )}
              </div>
            </CardHeader>
            <CardContent className="p-6">
              <div className="space-y-6">
                {phase.skills?.map((skill: any, skillIdx: number) => (
                  <div key={skillIdx} className="flex flex-col md:flex-row gap-6 p-4 rounded-lg border border-slate-100 bg-white shadow-sm hover:shadow-md transition-shadow">
                    
                    {/* Skill Info */}
                    <div className="md:w-1/3 space-y-3">
                      <div className="flex items-center gap-2">
                        <div className="bg-slate-900 text-white h-6 w-6 rounded flex items-center justify-center font-bold text-xs">
                          {skillIdx + 1}
                        </div>
                        <h4 className="font-semibold text-lg">{skill.skillName}</h4>
                      </div>
                      <div className="flex flex-wrap gap-2">
                        <Badge variant="outline">{skill.classification}</Badge>
                        <Badge className="bg-orange-100 text-orange-800 hover:bg-orange-200 border-none">
                          Priority {skill.priorityScore}
                        </Badge>
                      </div>
                      <div className="text-sm text-slate-600 flex items-center gap-2">
                        <Calendar className="h-4 w-4" /> {skill.learningHours} hours
                      </div>
                      {skill.dependency && (
                        <div className="text-sm text-slate-500 flex items-center gap-2 bg-slate-50 p-2 rounded">
                          <GitCommit className="h-4 w-4" /> Requires: {skill.dependency}
                        </div>
                      )}
                    </div>

                    {/* AI Explanation & Resources */}
                    <div className="md:w-2/3 space-y-4">
                      <p className="text-sm text-slate-700 leading-relaxed border-l-2 border-primary/20 pl-3">
                        {skill.aiExplanation}
                      </p>
                      
                      {skill.resources && skill.resources.length > 0 && (
                        <div>
                          <h5 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2 flex items-center gap-1">
                            <BookOpen className="h-3 w-3" /> Recommended Resources
                          </h5>
                          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                            {skill.resources.map((res: any, resIdx: number) => (
                              <a 
                                key={resIdx} 
                                href={res.url}
                                target="_blank"
                                rel="noreferrer"
                                className="flex items-center gap-2 text-sm p-2 rounded border hover:border-primary hover:bg-primary/5 transition-colors group"
                              >
                                <ExternalLink className="h-4 w-4 text-slate-400 group-hover:text-primary" />
                                <div className="truncate flex-1">
                                  <span className="font-medium text-slate-800">{res.title}</span>
                                  <span className="block text-xs text-slate-500 truncate">{res.type}</span>
                                </div>
                              </a>
                            ))}
                          </div>
                        </div>
                      )}
                      
                      {skill.projects && skill.projects.length > 0 && (
                        <div className="pt-2">
                          <h5 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2 flex items-center gap-1">
                            <Code className="h-3 w-3" /> Practice Projects
                          </h5>
                          <ul className="space-y-2">
                            {skill.projects.map((proj: any, pIdx: number) => (
                              <li key={pIdx} className="text-sm bg-slate-50 p-3 rounded-lg border flex gap-2">
                                <ArrowRight className="h-4 w-4 text-primary shrink-0 mt-0.5" />
                                <div>
                                  <div className="font-medium">{proj.name}</div>
                                  <div className="text-slate-600 mt-1">{proj.description}</div>
                                </div>
                              </li>
                            ))}
                          </ul>
                        </div>
                      )}

                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};
