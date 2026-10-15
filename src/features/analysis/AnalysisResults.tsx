import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api } from '@/services/api';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Progress } from '@/components/ui/progress';
import { CheckCircle2, XCircle, AlertCircle, TrendingUp } from 'lucide-react';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';

export const AnalysisResults: React.FC = () => {
  const [searchParams] = useSearchParams();
  const id = searchParams.get('id');
  const [analysis, setAnalysis] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) {
      setError('No analysis ID provided.');
      setLoading(false);
      return;
    }

    const fetchAnalysis = async () => {
      try {
        const response = await api.get(`/analysis/${id}`);
        setAnalysis(response.data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'An unexpected error occurred. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchAnalysis();
  }, [id]);

  if (loading) {
    return <div className="p-8 text-center text-muted-foreground">Loading analysis results...</div>;
  }

  if (error) {
    return (
      <div className="p-8 max-w-3xl mx-auto mt-8">
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      </div>
    );
  }

  if (!analysis) return null;

  // Map backend fields to display values
  const overallScore = Number(analysis.matchPercentage ?? 0);
  const scores = analysis.scores ?? {};
  const skillsScore = Number(scores.requiredSkillsScore ?? scores.skillsScore ?? 0);
  const experienceScore = Number(scores.experienceScore ?? 0);
  const educationScore = Number(scores.educationScore ?? 0);
  const keywordScore = Number(scores.keywordCoverageScore ?? scores.formatScore ?? 0);

  const matchingSkills: string[] = analysis.matchingSkills ?? [];
  const missingSkills: any[] = analysis.missingSkills ?? [];
  const recommendations: any[] = analysis.recommendations ?? [];
  const strengths: string[] = analysis.strengths ?? [];

  return (
    <div className="max-w-5xl mx-auto space-y-8 mt-8 pb-12">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Analysis Results</h2>
        <p className="text-muted-foreground">
          Detailed breakdown of your resume against the job description.
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        <Card className="md:col-span-1 border-primary/20 bg-primary/5">
          <CardHeader>
            <CardTitle className="text-lg">Overall Match Score</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col items-center justify-center py-6">
            <div className="relative flex items-center justify-center h-40 w-40">
              <svg className="w-full h-full transform -rotate-90" viewBox="0 0 36 36">
                <path
                  className="text-slate-200 stroke-current"
                  strokeWidth="3"
                  fill="none"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <path
                  className="text-primary stroke-current"
                  strokeWidth="3"
                  strokeDasharray={`${overallScore}, 100`}
                  fill="none"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
              </svg>
              <div className="absolute text-4xl font-bold">{Math.round(overallScore)}%</div>
            </div>
            <p className="mt-4 text-center font-medium text-primary">
              {overallScore >= 80 ? 'Excellent Match!' :
               overallScore >= 60 ? 'Good Match' : 'Needs Improvement'}
            </p>
          </CardContent>
        </Card>

        <Card className="md:col-span-2">
          <CardHeader>
            <CardTitle>Score Breakdown</CardTitle>
            <CardDescription>How your resume performed in key areas</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Skills Match</span>
                <span className="text-sm font-medium">{Math.round(skillsScore)}%</span>
              </div>
              <Progress value={skillsScore} className="h-2" />
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Experience Level</span>
                <span className="text-sm font-medium">{Math.round(experienceScore)}%</span>
              </div>
              <Progress value={experienceScore} className="h-2" />
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Education</span>
                <span className="text-sm font-medium">{Math.round(educationScore)}%</span>
              </div>
              <Progress value={educationScore} className="h-2" />
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium">Keyword Coverage</span>
                <span className="text-sm font-medium">{Math.round(keywordScore)}%</span>
              </div>
              <Progress value={keywordScore} className="h-2" />
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <CheckCircle2 className="h-5 w-5 text-green-600" />
              Strengths (Matched Skills)
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex flex-wrap gap-2">
              {matchingSkills.map((skill: string, idx: number) => (
                <Badge key={idx} variant="secondary" className="bg-green-100 text-green-800 hover:bg-green-200">
                  {skill}
                </Badge>
              ))}
              {strengths.length > 0 && matchingSkills.length === 0 && strengths.map((s: string, idx: number) => (
                <Badge key={idx} variant="secondary" className="bg-green-100 text-green-800 hover:bg-green-200">
                  {s}
                </Badge>
              ))}
              {matchingSkills.length === 0 && strengths.length === 0 && (
                <p className="text-sm text-muted-foreground">No matching skills identified.</p>
              )}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <XCircle className="h-5 w-5 text-red-600" />
              Missing Skills (Gaps)
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex flex-wrap gap-2">
              {missingSkills.map((skill: any, idx: number) => (
                <Badge key={idx} variant="outline" className="border-red-200 text-red-700 bg-red-50">
                  {typeof skill === 'string' ? skill : skill.skill ?? skill.name ?? JSON.stringify(skill)}
                </Badge>
              ))}
              {missingSkills.length === 0 && (
                <p className="text-sm text-muted-foreground">No missing skills identified!</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <TrendingUp className="h-5 w-5 text-blue-600" />
            Actionable Recommendations
          </CardTitle>
        </CardHeader>
        <CardContent>
          <ul className="space-y-4">
            {recommendations.map((rec: any, idx: number) => (
              <li key={idx} className="flex gap-3 text-sm">
                <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-blue-100 text-blue-700 font-medium text-xs">
                  {idx + 1}
                </span>
                <span className="pt-1 leading-relaxed">
                  {typeof rec === 'string' ? rec : rec.recommendation ?? rec.text ?? rec.description ?? JSON.stringify(rec)}
                </span>
              </li>
            ))}
            {recommendations.length === 0 && (
              <p className="text-sm text-muted-foreground">No recommendations available.</p>
            )}
          </ul>
        </CardContent>
      </Card>

      <div className="flex justify-center pt-8 gap-4">
        <a
          href={`/improvement?analysisId=${analysis.id}`}
          className="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-8 py-2 shadow"
        >
          Auto-Improve Resume Bullets
        </a>
        <a
          href={`/roadmap?analysisId=${analysis.id}`}
          className="inline-flex items-center justify-center rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border border-input bg-background hover:bg-accent hover:text-accent-foreground h-10 px-8 py-2"
        >
          View Skill Roadmap
        </a>
      </div>
    </div>
  );
};
