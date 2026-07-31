import React from 'react';
import { useSearchParams } from 'react-router-dom';
import { ResumeUpload } from '@/features/resume/ResumeUpload';
import { AnalysisResults } from '@/features/analysis/AnalysisResults';

export const AnalysisPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const id = searchParams.get('id');

  if (id) {
    return <AnalysisResults />;
  }

  return <ResumeUpload />;
};
