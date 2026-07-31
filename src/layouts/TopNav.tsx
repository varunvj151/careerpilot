import React from 'react';
import { useAuth } from '@/features/auth/AuthContext';
import { LogOut, User } from 'lucide-react';

export const TopNav: React.FC = () => {
  const { logout } = useAuth();

  return (
    <div className="sticky top-0 z-10 flex h-16 shrink-0 items-center gap-x-4 border-b border-slate-200 bg-white px-4 shadow-sm sm:gap-x-6 sm:px-6 lg:px-8">
      <div className="flex flex-1 gap-x-4 self-stretch lg:gap-x-6 justify-end">
        <div className="flex items-center gap-x-4 lg:gap-x-6">
          
          {/* Separator */}
          <div className="hidden lg:block lg:h-6 lg:w-px lg:bg-slate-200" aria-hidden="true" />

          {/* Profile dropdown stub */}
          <div className="flex items-center gap-x-4">
            <span className="sr-only">Your profile</span>
            <div className="h-8 w-8 rounded-full bg-slate-100 flex items-center justify-center text-slate-600">
              <User className="h-5 w-5" />
            </div>
            
            <button
              onClick={logout}
              className="text-sm font-semibold leading-6 text-slate-900 hover:text-slate-600 flex items-center gap-2"
            >
              <LogOut className="h-4 w-4" />
              Log out
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
