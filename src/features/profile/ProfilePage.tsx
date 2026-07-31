import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { User, Mail, Briefcase, Save } from 'lucide-react';
import toast from 'react-hot-toast';

export const ProfilePage: React.FC = () => {
  // In a real app, this data would come from AuthContext or an API call
  const [profile, setProfile] = useState({
    name: 'John Doe',
    email: 'john.doe@example.com',
    jobTitle: 'Software Engineer',
    bio: 'Passionate about building scalable applications and learning new technologies.',
  });

  const [isSaving, setIsSaving] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    // Mock API call
    setTimeout(() => {
      setIsSaving(false);
      toast.success('Profile updated successfully!');
    }, 1000);
  };

  const getInitials = (name: string) => {
    return name.split(' ').map(n => n[0]).join('').toUpperCase();
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 mt-8 pb-12">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Your Profile</h2>
        <p className="text-muted-foreground mt-2">
          Manage your personal information and resume defaults.
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-3">
        {/* Avatar Sidebar */}
        <Card className="md:col-span-1">
          <CardContent className="p-6 flex flex-col items-center text-center space-y-4">
            <Avatar className="h-32 w-32 border-4 border-slate-50">
              <AvatarImage src="" />
              <AvatarFallback className="text-4xl bg-primary/10 text-primary">
                {getInitials(profile.name)}
              </AvatarFallback>
            </Avatar>
            <div>
              <h3 className="font-semibold text-lg">{profile.name}</h3>
              <p className="text-sm text-slate-500">{profile.jobTitle}</p>
            </div>
            <Button variant="outline" size="sm" className="w-full">
              Change Picture
            </Button>
          </CardContent>
        </Card>

        {/* Form Details */}
        <Card className="md:col-span-2">
          <CardHeader>
            <CardTitle>Personal Information</CardTitle>
            <CardDescription>Update your contact details and professional summary.</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSave} className="space-y-6">
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="space-y-2">
                  <Label htmlFor="name" className="flex items-center gap-2">
                    <User className="h-4 w-4 text-slate-500" /> Full Name
                  </Label>
                  <Input 
                    id="name" 
                    value={profile.name} 
                    onChange={e => setProfile({...profile, name: e.target.value})} 
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="email" className="flex items-center gap-2">
                    <Mail className="h-4 w-4 text-slate-500" /> Email Address
                  </Label>
                  <Input 
                    id="email" 
                    type="email" 
                    value={profile.email} 
                    disabled // usually emails are not directly editable without verification
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="jobTitle" className="flex items-center gap-2">
                  <Briefcase className="h-4 w-4 text-slate-500" /> Target Job Title
                </Label>
                <Input 
                  id="jobTitle" 
                  value={profile.jobTitle} 
                  onChange={e => setProfile({...profile, jobTitle: e.target.value})} 
                  placeholder="e.g. Senior Frontend Developer"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="bio">Professional Summary</Label>
                <textarea 
                  id="bio"
                  rows={4}
                  className="flex w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  value={profile.bio}
                  onChange={e => setProfile({...profile, bio: e.target.value})}
                  placeholder="A brief summary of your experience..."
                />
              </div>

              <Button type="submit" disabled={isSaving} className="w-full sm:w-auto flex gap-2">
                <Save className="h-4 w-4" />
                {isSaving ? 'Saving...' : 'Save Changes'}
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
