import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { api } from '@/services/api';
import { useAuth } from './AuthContext';
import { Link, useNavigate } from 'react-router-dom';

const registerSchema = z.object({
  firstName: z.string().min(2, 'First name is required'),
  lastName: z.string().min(2, 'Last name is required'),
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
});

type RegisterFormValues = z.infer<typeof registerSchema>;

export const Register: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [serverError, setServerError] = useState('');

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormValues) => {
    try {
      setServerError('');
      const payload = {
        fullName: `${data.firstName} ${data.lastName}`.trim(),
        email: data.email,
        password: data.password
      };
      const response = await api.post('/auth/register', payload);
      login(response.data.token);
      navigate('/');
    } catch (error: any) {
      setServerError(error.response?.data?.message || 'Failed to register');
    }
  };

  return (
    <div>
      <h3 className="text-xl font-semibold mb-6 text-center">Create your account</h3>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium leading-6 text-slate-900">First name</label>
            <div className="mt-2">
              <input
                {...register('firstName')}
                type="text"
                className="block w-full rounded-md border-0 py-1.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6 px-3"
              />
              {errors.firstName && <p className="mt-1 text-sm text-red-600">{errors.firstName.message}</p>}
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium leading-6 text-slate-900">Last name</label>
            <div className="mt-2">
              <input
                {...register('lastName')}
                type="text"
                className="block w-full rounded-md border-0 py-1.5 text-slate-900 shadow-sm ring-1 ring-inset ring-slate-300 placeholder:text-slate-400 focus:ring-2 focus:ring-inset focus:ring-slate-600 sm:text-sm sm:leading-6 px-3"
              />
              {errors.lastName && <p className="mt-1 text-sm text-red-600">{errors.lastName.message}</p>}
            </div>
          </div>
        </div>

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
            {isSubmitting ? 'Creating account...' : 'Create account'}
          </button>
        </div>
      </form>

      <p className="mt-10 text-center text-sm text-slate-500">
        Already have an account?{' '}
        <Link to="/login" className="font-semibold leading-6 text-slate-900 hover:text-slate-600">
          Sign in
        </Link>
      </p>
    </div>
  );
};
