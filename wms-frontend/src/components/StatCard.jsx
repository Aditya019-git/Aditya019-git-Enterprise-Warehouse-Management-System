import React from 'react';

export default function StatCard({ title, value, icon: Icon, color }) {
  return (
    <div className="glass-card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <div>
        <p style={{ color: 'var(--text-muted)', fontSize: '14px', fontWeight: '500', marginBottom: '8px' }}>{title}</p>
        <h3 style={{ fontSize: '28px', fontWeight: '600' }}>{value}</h3>
      </div>
      <div style={{
        width: '48px', height: '48px', borderRadius: '12px',
        backgroundColor: `rgba(${color}, 0.1)`, display: 'flex',
        alignItems: 'center', justifyContent: 'center'
      }}>
        <Icon style={{ color: `rgb(${color})` }} size={22} />
      </div>
    </div>
  );
}