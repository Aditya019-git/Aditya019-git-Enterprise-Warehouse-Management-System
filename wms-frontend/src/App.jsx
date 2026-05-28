import React, { useState } from 'react';
import Sidebar from './components/Sidebar';
import StatCard from './components/StatCard';
import { Package, ShoppingCart, Layers, AlertCircle, Plus, Info } from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');

  // Static Mock Data for Day 18 Interface Styling (Connecting live APIs tomorrow!)
  const stats = [
    { title: 'Total Registered SKUs', value: '142', icon: Package, color: '99, 102, 241' },
    { title: 'Active Processing Orders', value: '18', icon: ShoppingCart, color: '168, 85, 247' },
    { title: 'Average Shelf Occupancy', value: '64%', icon: Layers, color: '16, 185, 129' },
    { title: 'Storage Capacity Alerts', value: '2', icon: AlertCircle, color: '239, 68, 68' }
  ];

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            {/* Header */}
            <div>
              <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Warehouse Overview</h1>
              <p style={{ color: 'var(--text-muted)' }}>Real-time capacity tracking and active floor logistics metrics.</p>
            </div>

            {/* Stats Grid */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '24px' }}>
              {stats.map((stat, i) => (
                <StatCard key={i} {...stat} />
              ))}
            </div>

            {/* Main Content Sections */}
            <div style={{ display: 'grid', gridTemplateColumns: '2fr 1.2fr', gap: '24px' }}>

              {/* Shelf Space Capacity Monitor */}
              <div className="glass-card">
                <h2 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '20px' }}>Active Bin Allocation Monitor</h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  {['Aisle A - Racks A1 to A4', 'Aisle B - Racks B1 to B4', 'Aisle C - Racks C1 to C4'].map((aisle, index) => {
                    const progress = [82, 45, 12][index];
                    const color = progress > 80 ? 'var(--danger)' : progress > 40 ? 'var(--accent-primary)' : 'var(--success)';
                    return (
                      <div key={index}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px', marginBottom: '6px' }}>
                          <span style={{ fontWeight: '500' }}>{aisle}</span>
                          <span style={{ color: 'var(--text-muted)' }}>{progress}% Occupied</span>
                        </div>
                        <div style={{ width: '100%', height: '8px', backgroundColor: 'rgba(255,255,255,0.05)', borderRadius: '4px', overflow: 'hidden' }}>
                          <div style={{ width: `${progress}%`, height: '100%', backgroundColor: color, borderRadius: '4px', transition: 'width 1s ease' }}></div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Floor Notices */}
              <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                <div>
                  <h2 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '16px' }}>System Notice Board</h2>
                  <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start', padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(239, 68, 68, 0.05)', border: '1px solid rgba(239, 68, 68, 0.1)', marginBottom: '12px' }}>
                    <AlertCircle size={18} style={{ color: 'var(--danger)', flexShrink: 0, marginTop: '2px' }} />
                    <p style={{ fontSize: '14px', color: 'var(--text-muted)' }}>
                      <strong style={{ color: 'var(--text-main)' }}>Aisle A Overcapacity:</strong> Bins A3 and A4 are approaching limit thresholds. Re-route putaways.
                    </p>
                  </div>
                  <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start', padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(99, 102, 241, 0.05)', border: '1px solid rgba(99, 102, 241, 0.1)' }}>
                    <Info size={18} style={{ color: 'var(--accent-primary)', flexShrink: 0, marginTop: '2px' }} />
                    <p style={{ fontSize: '14px', color: 'var(--text-muted)' }}>
                      <strong style={{ color: 'var(--text-main)' }}>System Upgrade:</strong> Role-Based Access Control and Database Concurrency Lock mechanisms are online.
                    </p>
                  </div>
                </div>
                <div style={{ fontSize: '12px', color: 'var(--text-muted)', textAlign: 'center', marginTop: '20px' }}>
                  WMS Backend Connected: <span style={{ color: 'var(--success)' }}>ONLINE</span>
                </div>
              </div>

            </div>
          </div>
        );
      case 'products':
        return (
          <div>
            <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Product Catalog</h1>
            <p style={{ color: 'var(--text-muted)' }}>Configure and register merchandise SKUs in the WMS dictionary.</p>
          </div>
        );
      case 'orders':
        return (
          <div>
            <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Fulfillment Manager</h1>
            <p style={{ color: 'var(--text-muted)' }}>Live order checking, picking lists, and state-machine transitions.</p>
          </div>
        );
      case 'bins':
        return (
          <div>
            <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Bin Visualizer</h1>
            <p style={{ color: 'var(--text-muted)' }}>Interactive layout grid of physical shelf storage bins.</p>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div>
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />
      <div style={{ marginLeft: '260px', padding: '40px', minHeight: '100vh' }}>
        {renderContent()}
      </div>
    </div>
  );
}