import { describe, it, expect, beforeEach } from 'vitest';
import { api } from './api';

describe('API Service', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('is configured with proper defaults', () => {
    expect(api.defaults.headers['Content-Type']).toBe('application/json');
    expect(api.defaults.baseURL).toBeDefined();
  });

  it('attaches Bearer token from localStorage to requests', async () => {
    localStorage.setItem('token', 'sample-jwt-token');

    // Test request interceptor
    const requestInterceptor = (api.interceptors.request as any).handlers[0];
    const config = await requestInterceptor.fulfilled({ headers: {} });

    expect(config.headers.Authorization).toBe('Bearer sample-jwt-token');
  });

  it('does not attach Bearer token if token is absent', async () => {
    const requestInterceptor = (api.interceptors.request as any).handlers[0];
    const config = await requestInterceptor.fulfilled({ headers: {} });

    expect(config.headers.Authorization).toBeUndefined();
  });
});
