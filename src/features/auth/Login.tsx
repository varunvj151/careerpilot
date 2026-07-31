import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { api } from '@/services/api';
import { useAuth } from './AuthContext';
import { Link, useNavigate } from 'react-router-dom';

const loginSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(1, 'Password is required'),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export const Login: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [serverError, setServerError] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormValues) => {
    try {
      setServerError('');
      // Adjust backend endpoint if necessary
      const response = await api.post('/auth/login', data);
      login(response.data.token);
      navigate('/');
    } catch (error: any) {
      setServerError(error.response?.data?.message || 'Failed to login');
    }
  };

  return (
    <div>
      <h3 className="text-xl font-semibold mb-6 text-center">Sign in to your account</h3>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div>
          <label className="block text-sm font-medium leading-6 text-slate-900">Email address</label>
          <div className="mt-2">
            <input
              {...register('email')}
              type="email"
              className="block w-full rounded-md border-0 py-1.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6 px-3"
            />
            {errors.email && <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>}
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium leading-6 text-slate-900">Password</label>
          <div className="mt-2">
            <input
              {...register('password')}
              type="password"
              className="block w-full rounded-md border-0 py-1.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6 px-3"
            />
            {errors.password && <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>}
          </div>
        </div>

        {serverError && <div className="text-sm text-red-600 text-center">{serverError}</div>}

        <div>
          <button
            type="submit"
            disabled={isSubmitting}
            className="flex w-full justify-center rounded-md bg-slate-900 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-slate-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-slate-600 disabled:opacity-50"
          >
            {isSubmitting ? 'Signing in...' : 'Sign in'}
          </button>
        </div>
      </form>

      <p className="mt-10 text-center text-sm text-slate-500">
        Not a member?{' '}
        <Link to="/register" className="font-semibold leading-6 text-slate-900 hover:text-slate-600">
          Create an account
        </Link>
      </p>
    </div>
  );
};
