import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Home, FileText, BarChart2, TrendingUp, History, Settings } from 'lucide-react';
import { cn } from '@/utils'; // assuming a classnames utility

const navigation = [
  { name: 'Dashboard', href: '/', icon: Home },
  { name: 'Resume Analysis', href: '/analysis', icon: FileText },
  { name: 'Improvement', href: '/improvement', icon: TrendingUp },
  { name: 'Skill Roadmap', href: '/roadmap', icon: BarChart2 },
  { name: 'History', href: '/history', icon: History },
];

export const Sidebar: React.FC = () => {
  const location = useLocation();

  return (
    <div className="flex h-full w-64 flex-col bg-slate-900 text-white">
      <div className="flex h-16 shrink-0 items-center px-6">
        <span className="text-xl font-bold tracking-tight">CareerPilot AI</span>
      </div>
      <div className="flex flex-1 flex-col overflow-y-auto">
        <nav className="flex-1 space-y-1 px-4 py-4">
          {navigation.map((item) => {
            const isActive = location.pathname === item.href || (item.href !== '/' && location.pathname.startsWith(item.href));
            return (
              <Link
                key={item.name}
                to={item.href}
                className={cn(
                  isActive ? 'bg-slate-800 text-white' : 'text-slate-300 hover:bg-slate-800 hover:text-white',
                  'group flex items-center rounded-md px-2 py-2 text-sm font-medium transition-colors'
                )}
              >
                <item.icon
                  className={cn(
                    isActive ? 'text-white' : 'text-slate-400 group-hover:text-white',
                    'mr-3 h-5 w-5 flex-shrink-0 transition-colors'
                  )}
                  aria-hidden="true"
                />
                {item.name}
              </Link>
            );
          })}
        </nav>
      </div>
      <div className="border-t border-slate-800 p-4">
        <Link
          to="/profile"
          className="group flex items-center rounded-md px-2 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white transition-colors"
        >
          <Settings className="mr-3 h-5 w-5 text-slate-400 group-hover:text-white transition-colors" />
          Settings
        </Link>
      </div>
    </div>
  );
};
