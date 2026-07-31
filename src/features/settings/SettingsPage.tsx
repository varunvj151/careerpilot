import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Button } from '@/components/ui/button';
import { Bell, Sun, Lock, Shield } from 'lucide-react';
import toast from 'react-hot-toast';

export const SettingsPage: React.FC = () => {
  const [settings, setSettings] = useState({
    emailNotifications: true,
    weeklyReports: false,
    darkMode: false,
    twoFactor: false,
  });

  const toggleDarkMode = (checked: boolean) => {
    setSettings({ ...settings, darkMode: checked });
    if (checked) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  };

  const handleSave = () => {
    toast.success('Settings saved successfully!');
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8 mt-8 pb-12">
      <div>
        <h2 className="text-3xl font-bold tracking-tight">Settings</h2>
        <p className="text-muted-foreground mt-2">
          Manage your account preferences, appearance, and security.
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        {/* Preferences */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Sun className="h-5 w-5" /> Appearance
            </CardTitle>
            <CardDescription>Customize how CareerPilot looks on your device.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex items-center justify-between">
              <div className="space-y-0.5">
                <Label>Dark Mode</Label>
                <div className="text-sm text-slate-500">Toggle dark mode interface</div>
              </div>
              <Switch 
                checked={settings.darkMode}
                onCheckedChange={toggleDarkMode}
              />
            </div>
          </CardContent>
        </Card>

        {/* Notifications */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Bell className="h-5 w-5" /> Notifications
            </CardTitle>
            <CardDescription>Control what emails we send you.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex items-center justify-between">
              <div className="space-y-0.5">
                <Label>Email Alerts</Label>
                <div className="text-sm text-slate-500">Receive updates on job matches</div>
              </div>
              <Switch 
                checked={settings.emailNotifications}
                onCheckedChange={(c) => setSettings({...settings, emailNotifications: c})}
              />
            </div>
            <div className="flex items-center justify-between">
              <div className="space-y-0.5">
                <Label>Weekly Reports</Label>
                <div className="text-sm text-slate-500">Get a summary of your activity</div>
              </div>
              <Switch 
                checked={settings.weeklyReports}
                onCheckedChange={(c) => setSettings({...settings, weeklyReports: c})}
              />
            </div>
          </CardContent>
        </Card>

        {/* Security */}
        <Card className="md:col-span-2 border-red-100">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-red-700">
              <Shield className="h-5 w-5" /> Security
            </CardTitle>
            <CardDescription>Keep your account secure.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="flex items-center justify-between p-4 bg-slate-50 rounded-lg border">
              <div className="space-y-1">
                <Label className="flex items-center gap-2"><Lock className="h-4 w-4" /> Change Password</Label>
                <div className="text-sm text-slate-500">Ensure you use a strong, unique password.</div>
              </div>
              <Button variant="outline">Update</Button>
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="flex justify-end pt-4 border-t">
        <Button onClick={handleSave} size="lg">Save Preferences</Button>
      </div>
    </div>
  );
};
