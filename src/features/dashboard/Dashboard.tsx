import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useAuth } from '../auth/AuthContext';
import { FileText, Activity, TrendingUp, Clock } from 'lucide-react';
import { Link } from 'react-router-dom';
import { api } from '@/services/api';
import { Button } from '@/components/ui/button';

export const Dashboard: React.FC = () => {
  useAuth();
  const [stats, setStats] = useState({ total: 0, avgScore: 0, lastScore: 0 });
  const [recentAnalyses, setRecentAnalyses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const res = await api.get('/analysis/history');
        const list: any[] = Array.isArray(res.data) ? res.data : res.data.content || [];
        setRecentAnalyses(list.slice(0, 5));
        if (list.length > 0) {
          const scores = list.map((a: any) => Number(a.matchPercentage || 0));
          const avg = scores.reduce((a: number, b: number) => a + b, 0) / scores.length;
          setStats({
            total: list.length,
            avgScore: Math.round(avg),
            lastScore: Math.round(scores[0]),
          });
        }
      } catch {
        // Silently ignore — user may have no analyses yet
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const formatDate = (dateString: string) => {
    return new Intl.DateTimeFormat('en-US', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(dateString));
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">Dashboard</h2>
        <p className="text-muted-foreground">
          Welcome back! Here's an overview of your career progression.
        </p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Analyses</CardTitle>
            <Activity className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '—' : stats.total}</div>
            <p className="text-xs text-muted-foreground">Resume analyses run</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Avg Match Score</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '—' : stats.total > 0 ? `${stats.avgScore}%` : 'N/A'}</div>
            <p className="text-xs text-muted-foreground">Across all analyses</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Latest Score</CardTitle>
            <FileText className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{loading ? '—' : stats.total > 0 ? `${stats.lastScore}%` : 'N/A'}</div>
            <p className="text-xs text-muted-foreground">Most recent analysis</p>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
        <Card className="col-span-4">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Clock className="h-5 w-5" /> Recent Analyses
            </CardTitle>
          </CardHeader>
          <CardContent>
            {loading ? (
              <p className="text-sm text-muted-foreground">Loading...</p>
            ) : recentAnalyses.length === 0 ? (
              <div className="text-center py-6 space-y-3">
                <p className="text-sm text-muted-foreground">No analyses yet. Upload a resume to get started!</p>
                <Link to="/resume-analysis">
                  <Button size="sm">Analyze Your Resume</Button>
                </Link>
              </div>
            ) : (
              <div className="space-y-4">
                {recentAnalyses.map((item: any) => (
                  <Link
                    key={item.id}
                    to={`/analysis?id=${item.id}`}
                    className="flex items-center gap-3 p-3 rounded-lg hover:bg-slate-50 transition-colors group"
                  >
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium leading-none truncate">
                        {item.jobDescription?.title || 'General Analysis'}
                      </p>
                      <p className="text-xs text-muted-foreground mt-1">
                        {item.jobDescription?.company ? `${item.jobDescription.company} · ` : ''}{formatDate(item.createdAt)}
                      </p>
                    </div>
                    <div className={`text-sm font-bold px-2 py-0.5 rounded ${
                      Number(item.matchPercentage) >= 70 
                        ? 'bg-green-100 text-green-700' 
                        : 'bg-orange-100 text-orange-700'
                    }`}>
                      {item.matchPercentage ? `${Math.round(Number(item.matchPercentage))}%` : 'N/A'}
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
        
        <Card className="col-span-3">
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            <Link
              to="/resume-analysis"
              className="flex items-center justify-center rounded-md bg-slate-900 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-slate-700 transition-colors"
            >
              New Resume Analysis
            </Link>
            <Link
              to="/history"
              className="flex items-center justify-center rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 shadow-sm hover:bg-slate-50 transition-colors"
            >
              View Full History
            </Link>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
