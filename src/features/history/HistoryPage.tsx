import React, { useEffect, useState } from 'react';
import { api } from '@/services/api';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Eye, Clock, FileText, ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';

export const HistoryPage: React.FC = () => {
  const [history, setHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      // Assuming a GET /api/v1/analysis or GET /api/v1/resumes endpoint that lists past analyses
      // For this implementation, let's assume we have an endpoint like that. 
      // If it returns a paginated list or list of AnalysisResponse
      const response = await api.get('/analysis');
      // Adjust according to backend paginated response vs list
      setHistory(Array.isArray(response.data) ? response.data : response.data.content || []);
      setError(null);
    } catch (err: any) {
      if (err.response?.status === 404) {
        setHistory([]);
      } else {
        setError(err.response?.data?.message || 'Failed to load history.');
      }
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Intl.DateTimeFormat('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    }).format(new Date(dateString));
  };

  if (loading) {
    return <div className="p-8 text-center text-muted-foreground">Loading history...</div>;
  }

  return (
    <div className="max-w-6xl mx-auto space-y-6 mt-8 pb-12">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Analysis History</h2>
        <p className="text-muted-foreground mt-2">
          Review your previous resume analyses, improvements, and roadmaps.
        </p>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Clock className="h-5 w-5" /> Past Analyses
          </CardTitle>
          <CardDescription>Your recently uploaded and analyzed resumes.</CardDescription>
        </CardHeader>
        <CardContent>
          {history.length === 0 ? (
            <div className="text-center py-12 space-y-4">
              <FileText className="h-12 w-12 text-slate-300 mx-auto" />
              <div className="text-lg font-medium text-slate-900">No analyses found</div>
              <p className="text-slate-500">You haven't analyzed any resumes yet.</p>
              <Button onClick={() => navigate('/')}>Analyze a Resume</Button>
            </div>
          ) : (
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Date</TableHead>
                    <TableHead>Target Job Title</TableHead>
                    <TableHead>Match Score</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {history.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell className="font-medium">
                        {formatDate(item.createdAt)}
                      </TableCell>
                      <TableCell>{item.jobDescription?.title || 'General Analysis'}</TableCell>
                      <TableCell>
                        <Badge 
                          variant={item.matchPercentage >= 70 ? 'default' : 'secondary'}
                          className={item.matchPercentage >= 70 ? 'bg-green-100 text-green-800 hover:bg-green-200' : ''}
                        >
                          {item.matchPercentage ? `${Math.round(item.matchPercentage)}%` : 'N/A'}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right space-x-2">
                        <Button 
                          variant="ghost" 
                          size="sm"
                          onClick={() => navigate(`/analysis?id=${item.id}`)}
                          className="hover:text-primary hover:bg-primary/5"
                        >
                          <Eye className="h-4 w-4 mr-2" /> Results
                        </Button>
                        <Button 
                          variant="ghost" 
                          size="sm"
                          onClick={() => navigate(`/roadmap?analysisId=${item.id}`)}
                          className="hover:text-primary hover:bg-primary/5"
                        >
                          <ArrowRight className="h-4 w-4 mr-2" /> Roadmap
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
