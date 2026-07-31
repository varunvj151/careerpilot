import React from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { useAuth } from '@/features/auth/AuthContext';

export const AuthLayout: React.FC = () => {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 py-12 sm:px-6 lg:px-8">
      <div className="w-full max-w-md space-y-8 bg-white p-8 shadow rounded-lg">
        <div className="text-center">
          <h2 className="mt-6 text-3xl font-bold tracking-tight text-slate-900">CareerPilot AI</h2>
        </div>
        <Outlet />
      </div>
    </div>
  );
};
