import { useState, useEffect } from 'react';
import { api } from '@/services/api';

export type JobStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';

export interface JobResponse<T = any> {
  jobId: string;
  status: JobStatus;
  progress: number;
  result?: T;
  error?: string;
  createdAt: string;
  updatedAt: string;
}

export function useJobPolling<T>(jobId: string | null, intervalMs = 2000) {
  const [data, setData] = useState<JobResponse<T> | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isPolling, setIsPolling] = useState(false);

  useEffect(() => {
    if (!jobId) {
      setIsPolling(false);
      setData(null);
      setError(null);
      return;
    }

    setIsPolling(true);
    let timeoutId: number;

    const poll = async () => {
      try {
        const response = await api.get<JobResponse<T>>(`/jobs/${jobId}`);
        const job = response.data;
        setData(job);

        if (job.status === 'COMPLETED' || job.status === 'FAILED') {
          setIsPolling(false);
          if (job.status === 'FAILED') {
            setError(job.error || 'Job failed processing');
          }
        } else {
          // Continue polling
          timeoutId = setTimeout(poll, intervalMs);
        }
      } catch (err: any) {
        setIsPolling(false);
        setError(err.response?.data?.message || err.message || 'Failed to poll job status');
      }
    };

    poll();

    return () => {
      if (timeoutId) clearTimeout(timeoutId);
    };
  }, [jobId, intervalMs]);

  return { data, error, isPolling };
}
