import React, { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { UploadCloud, File, AlertCircle } from 'lucide-react';
import { api } from '@/services/api';
import { useJobPolling } from '@/hooks/useJobPolling';
import { Progress } from '@/components/ui/progress';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { useNavigate } from 'react-router-dom';

export const ResumeUpload: React.FC = () => {
  const [file, setFile] = useState<File | null>(null);
  const [jobDescription, setJobDescription] = useState('');
  const [uploadError, setUploadError] = useState<string | null>(null);
  const [jobId, setJobId] = useState<string | null>(null);
  const navigate = useNavigate();

  const { data: jobData, error: pollError, isPolling } = useJobPolling<any>(jobId);

  const onDrop = useCallback((acceptedFiles: File[]) => {
    if (acceptedFiles.length > 0) {
      setFile(acceptedFiles[0]);
      setUploadError(null);
    }
  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
    },
    maxFiles: 1,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) {
      setUploadError('Please select a resume PDF file.');
      return;
    }

    setUploadError(null);
    setJobId(null);

    const formData = new FormData();
    formData.append('file', file);
    if (jobDescription) {
      formData.append('jobDescription', jobDescription);
    }

    try {
      const response = await api.post('/resumes/analyze', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.status === 202 && response.data.jobId) {
        setJobId(response.data.jobId);
      } else if (response.status === 200 && response.data.id) {
         // Fallback if backend processes synchronously
         navigate(`/analysis?id=${response.data.id}`);
      }
    } catch (error: any) {
      setUploadError(error.response?.data?.message || 'An error occurred during upload.');
    }
  };

  // If job completed successfully, redirect to analysis page with the result ID
  React.useEffect(() => {
    if (jobData?.status === 'COMPLETED' && jobData.result?.id) {
      // Small delay to show 100% completion before redirect
      setTimeout(() => {
        navigate(`/analysis?id=${jobData.result.id}`);
      }, 1000);
    }
  }, [jobData, navigate]);

  return (
    <div className="max-w-3xl mx-auto space-y-6 mt-8">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">Resume Analysis</h2>
        <p className="text-muted-foreground">
          Upload your resume and the target job description to get a comprehensive match analysis.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Upload Documents</CardTitle>
          <CardDescription>We accept PDF format for resumes.</CardDescription>
        </CardHeader>
        <CardContent>
          {isPolling ? (
            <div className="space-y-6 py-8">
              <div className="text-center space-y-2">
                <h3 className="text-lg font-semibold">Analyzing your resume...</h3>
                <p className="text-sm text-muted-foreground">
                  Our AI is comparing your profile against the job description. This may take a minute.
                </p>
              </div>
              <Progress value={jobData?.progress || 10} className="w-full" />
              <div className="text-center text-sm font-medium text-slate-600">
                {jobData?.status === 'PROCESSING' ? 'Processing...' : 'Starting...'}
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="space-y-2">
                <Label>Resume PDF *</Label>
                <div
                  {...getRootProps()}
                  className={`border-2 border-dashed rounded-lg p-10 text-center cursor-pointer transition-colors
                    ${isDragActive ? 'border-primary bg-primary/5' : 'border-slate-300 hover:border-primary/50 hover:bg-slate-50'}
                  `}
                >
                  <input {...getInputProps()} />
                  {file ? (
                    <div className="flex flex-col items-center space-y-2">
                      <File className="h-10 w-10 text-primary" />
                      <p className="text-sm font-medium">{file.name}</p>
                      <p className="text-xs text-muted-foreground">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
                    </div>
                  ) : (
                    <div className="flex flex-col items-center space-y-2">
                      <UploadCloud className="h-10 w-10 text-slate-400" />
                      <p className="text-sm font-medium">Drag & drop your resume here, or click to select</p>
                      <p className="text-xs text-muted-foreground">Only .pdf files are supported</p>
                    </div>
                  )}
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="jobDescription">Target Job Description (Optional but recommended)</Label>
                <Textarea
                  id="jobDescription"
                  placeholder="Paste the job description here..."
                  className="min-h-[150px]"
                  value={jobDescription}
                  onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setJobDescription(e.target.value)}
                />
              </div>

              {(uploadError || pollError || jobData?.status === 'FAILED') && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertTitle>Error</AlertTitle>
                  <AlertDescription>{uploadError || pollError || jobData?.error}</AlertDescription>
                </Alert>
              )}

              <Button type="submit" className="w-full" disabled={!file}>
                Analyze Resume
              </Button>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
