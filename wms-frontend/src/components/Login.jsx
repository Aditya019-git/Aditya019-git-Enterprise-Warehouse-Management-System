import React, { useState } from 'react';
import api from '../api';
import { Lock, User, AlertCircle } from 'lucide-react';

export default function Login({ onLoginSuccess }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      // 1. Post credentials to Spring Boot backend
      const response = await api.post('/api/auth/login', { username, password });

      // 2. Extract and save the JWT token
      localStorage.setItem('wms_token', response.data.token);
      localStorage.setItem('wms_username', username);

      // 3. Notify parent app of successful authentication
      onLoginSuccess();
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid username or password');
    }
  };

  return (
    <div style={{
      height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'radial-gradient(circle, #0f172a 0%, #020617 100%)'
    }}>
      <form onSubmit={handleSubmit} className="glass-card" style={{ width: '400px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div style={{ textAlign: 'center', marginBottom: '10px' }}>
          <div style={{
            width: '48px', height: '48px', borderRadius: '12px', margin: '0 auto 16px',
            background: 'linear-gradient(135deg, var(--accent-primary), var(--accent-secondary))',
            display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold', fontSize: '20px'
          }}>W</div>
          <h2 style={{ fontSize: '24px', fontWeight: '600' }}>WMS Portal Login</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '14px', marginTop: '6px' }}>Enter your credentials to secure floor access.</p>
        </div>

        {error && (
          <div style={{ display: 'flex', gap: '8px', alignItems: 'center', padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(239, 68, 68, 0.05)', border: '1px solid rgba(239, 68, 68, 0.1)' }}>
            <AlertCircle size={18} style={{ color: 'var(--danger)' }} />
            <span style={{ fontSize: '14px', color: 'var(--danger)' }}>{error}</span>
          </div>
        )}

        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
          <label style={{ fontSize: '14px', fontWeight: '500', color: 'var(--text-muted)' }}>Username</label>
          <div style={{ position: 'relative' }}>
            <User size={16} style={{ position: 'absolute', left: '14px', top: '14px', color: 'var(--text-muted)' }} />
            <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required style={{
              width: '100%', padding: '12px 12px 12px 42px', borderRadius: '12px', border: '1px solid var(--glass-border)',
              backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', outline: 'none', fontSize: '15px'
            }} />
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
          <label style={{ fontSize: '14px', fontWeight: '500', color: 'var(--text-muted)' }}>Password</label>
          <div style={{ position: 'relative' }}>
            <Lock size={16} style={{ position: 'absolute', left: '14px', top: '14px', color: 'var(--text-muted)' }} />
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required style={{
              width: '100%', padding: '12px 12px 12px 42px', borderRadius: '12px', border: '1px solid var(--glass-border)',
              backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', outline: 'none', fontSize: '15px'
            }} />
          </div>
        </div>

        <button type="submit" style={{
          width: '100%', padding: '14px', borderRadius: '12px', border: 'none', cursor: 'pointer',
          background: 'linear-gradient(135deg, var(--accent-primary), var(--accent-secondary))',
          color: 'var(--text-main)', fontSize: '16px', fontWeight: '600', marginTop: '10px'
        }}>
          Authenticate Session
        </button>
      </form>
    </div>
  );
}