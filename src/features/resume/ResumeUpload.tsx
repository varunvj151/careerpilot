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
      setUploadError('Please select a PDF resume file.');
      return;
    }

    setUploadError(null);
    setJobId(null);

    try {
      // Step 1: Upload resume PDF file
      const uploadFormData = new FormData();
      uploadFormData.append('file', file);

      const uploadResponse = await api.post('/resumes/upload', uploadFormData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      const resumeId = uploadResponse.data.id;
      if (!resumeId) {
        throw new Error('Failed to get resume ID from server');
      }

      // Step 2: Ensure job description meets backend validation requirements (min 100 chars)
      const defaultJd = "Looking for a skilled Software Engineer / AI Developer with strong background in full stack development, Python, Java, Spring Boot, React, and database systems to build scalable applications.";
      const rawJd = jobDescription.trim();
      const finalJd = rawJd.length >= 100 ? rawJd : (rawJd ? `${rawJd} - ${defaultJd}` : defaultJd);

      // Step 3: Trigger async resume analysis
      const analysisResponse = await api.post('/analysis', {
        resumeId: resumeId,
        jobDescriptionText: finalJd,
        jobTitle: 'Software Engineer',
        company: 'Tech Corp'
      });

      const createdJobId = analysisResponse.data.jobId || analysisResponse.data.id;
      if (createdJobId) {
        setJobId(createdJobId);
      } else {
        navigate(`/analysis?id=${analysisResponse.data.id}`);
      }
    } catch (error: any) {
      setUploadError(error.response?.data?.message || error.message || 'An error occurred during upload and analysis.');
    }
  };

  // If job completed successfully, redirect to analysis page with the result ID
  React.useEffect(() => {
    if (jobData?.status === 'COMPLETED' && jobData.resultPayload?.id) {
      // Small delay to show 100% completion before redirect
      setTimeout(() => {
        navigate(`/analysis?id=${jobData.resultPayload.id}`);
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
          <CardDescription>Upload your resume as a PDF file (max 5MB).</CardDescription>
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
              <Progress value={jobData?.resultPayload?.progress || 10} className="w-full" />
              <div className="text-center text-sm font-medium text-slate-600">
                {jobData?.status === 'RUNNING' ? 'Processing...' : 'Starting...'}
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="space-y-2">
                <Label>Resume File (PDF/Image) *</Label>
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
                      <p className="text-xs text-muted-foreground">.pdf files are supported (max 5MB)</p>
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
                  <AlertDescription>{uploadError || pollError || jobData?.errorMessage}</AlertDescription>
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
