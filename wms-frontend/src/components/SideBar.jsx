import React from 'react';
import { LayoutDashboard, Package, ShoppingCart, Layers, Users, LogOut } from 'lucide-react';

export default function Sidebar({ activeTab, setActiveTab, handleSignOut }) {
  const username = localStorage.getItem('wms_username');
  const isAdmin = username === 'admin';

  // Base menu items
  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'products', label: 'Products', icon: Package },
    { id: 'orders', label: 'Orders', icon: ShoppingCart },
    { id: 'bins', label: 'Storage Bins', icon: Layers }
  ];

  // Dynamic Item: Only allow Admins to see User Management in their Sidebar
  if (isAdmin) {
    menuItems.push({ id: 'users', label: 'Staff Registry', icon: Users });
  }

  return (
    <div style={{
      width: '260px', height: '100vh', position: 'fixed', left: 0, top: 0,
      backgroundColor: 'var(--bg-secondary)', borderRight: '1px solid var(--glass-border)',
      padding: '24px 16px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between'
    }}>
      <div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '40px', paddingLeft: '8px' }}>
          <div style={{
            width: '36px', height: '36px', borderRadius: '8px', 
            background: 'linear-gradient(135deg, var(--accent-primary), var(--accent-secondary))',
            display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold'
          }}>W</div>
          <span style={{ fontSize: '20px', fontWeight: '600', letterSpacing: '0.5px' }}>WMS Floor</span>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {menuItems.map(item => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <button key={item.id} onClick={() => setActiveTab(item.id)} style={{
                width: '100%', display: 'flex', alignItems: 'center', gap: '14px',
                padding: '12px 16px', borderRadius: '12px', border: 'none', cursor: 'pointer',
                backgroundColor: isActive ? 'rgba(99, 102, 241, 0.1)' : 'transparent',
                color: isActive ? 'var(--text-main)' : 'var(--text-muted)',
                fontSize: '15px', fontWeight: '500', transition: 'all 0.2s ease', textAlign: 'left'
              }}>
                <Icon size={18} style={{ color: isActive ? 'var(--accent-primary)' : 'inherit' }} />
                {item.label}
              </button>
            );
          })}
        </div>
      </div>

      <button onClick={handleSignOut} style={{
        width: '100%', display: 'flex', alignItems: 'center', gap: '14px',
        padding: '12px 16px', borderRadius: '12px', border: 'none', cursor: 'pointer',
        backgroundColor: 'transparent', color: 'var(--danger)', fontSize: '15px', fontWeight: '500'
      }}>
        <LogOut size={18} />
        Sign Out
      </button>
    </div>
  );
}