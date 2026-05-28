import React, { useState, useEffect } from 'react';
import Sidebar from './components/Sidebar';
import StatCard from './components/StatCard';
import Login from './components/Login';
import api from './api';
import { Package, ShoppingCart, Layers, AlertCircle, Plus, Info, RefreshCw, Building2, MapPin } from 'lucide-react';

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('wms_token'));
  const [activeTab, setActiveTab] = useState('dashboard');
  
  // Real-time Database States
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [bins, setBins] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [stats, setStats] = useState([
    { title: 'Total Registered SKUs', value: '0', icon: Package, color: '99, 102, 241' },
    { title: 'Active Processing Orders', value: '0', icon: ShoppingCart, color: '168, 85, 247' },
    { title: 'Average Shelf Occupancy', value: '0%', icon: Layers, color: '16, 185, 129' },
    { title: 'Storage Capacity Alerts', value: '0', icon: AlertCircle, color: '239, 68, 68' }
  ]);

  // Loading indicator states
  const [isLoading, setIsLoading] = useState(false);

  // Form States - Product Creation
  const [prodName, setProdName] = useState('');
  const [prodSku, setProdSku] = useState('');
  const [prodDesc, setProdDesc] = useState('');
  const [prodPrice, setProdPrice] = useState('');
  const [prodWeight, setProdWeight] = useState('');
  const [prodSuccess, setProdSuccess] = useState('');
  const [prodError, setProdError] = useState('');

  // Form States - Warehouse Creation
  const [whName, setWhName] = useState('');
  const [whLoc, setWhLoc] = useState('');
  const [whCap, setWhCap] = useState('');
  const [whSuccess, setWhSuccess] = useState('');
  const [whError, setWhError] = useState('');

  // Form States - Bin Creation
  const [binCodeVal, setBinCodeVal] = useState('');
  const [binMaxCap, setBinMaxCap] = useState('');
  const [binWhId, setBinWhId] = useState('');
  const [binSuccess, setBinSuccess] = useState('');
  const [binError, setBinError] = useState('');

  // Form States - Order Creation
  const [orderNum, setOrderNum] = useState('');
  const [orderCust, setOrderCust] = useState('');
  const [orderProdId, setOrderProdId] = useState('');
  const [orderQty, setOrderQty] = useState('');
  const [orderSuccess, setOrderSuccess] = useState('');
  const [orderError, setOrderError] = useState('');

  // Staff Registration Form States
  const [staffUsername, setStaffUsername] = useState('');
  const [staffPassword, setStaffPassword] = useState('');
  const [staffRole, setStaffRole] = useState('ROLE_OPERATOR');
  const [staffSuccess, setStaffSuccess] = useState('');
  const [staffError, setStaffError] = useState('');

  useEffect(() => {
    if (isAuthenticated) {
      fetchWmsData();
    }
  }, [isAuthenticated]);

  const fetchWmsData = async () => {
    setIsLoading(true);
    try {
      const [prodRes, orderRes, binRes, whRes] = await Promise.all([
        api.get('/api/products'),
        api.get('/api/orders'),
        api.get('/api/bins'),
        api.get('/api/warehouses').catch(() => ({ data: [] })) // Avoid crash if no permissions or empty
      ]);

      setProducts(prodRes.data);
      setOrders(orderRes.data);
      if (binRes) setBins(binRes.data);
      if (whRes) setWarehouses(whRes.data);

      const activeOrdersCount = orderRes.data.filter(o => o.status !== 'SHIPPED').length;

      // Calculate dynamic storage stats
      let totalMaxCapacity = 0;
      let totalCurrentOccupancy = 0;
      let capacityAlertsCount = 0;

      if (binRes && binRes.data) {
        binRes.data.forEach(bin => {
          totalMaxCapacity += bin.maxCapacity || 0;
          totalCurrentOccupancy += bin.currentOccupancy || 0;
          if (bin.currentOccupancy >= bin.maxCapacity) {
            capacityAlertsCount++;
          }
        });
      }

      const averageOccupancy = totalMaxCapacity > 0 
        ? Math.round((totalCurrentOccupancy / totalMaxCapacity) * 100) 
        : 0;

      setStats([
        { title: 'Total Registered SKUs', value: prodRes.data.length.toString(), icon: Package, color: '99, 102, 241' },
        { title: 'Active Processing Orders', value: activeOrdersCount.toString(), icon: ShoppingCart, color: '168, 85, 247' },
        { title: 'Average Shelf Occupancy', value: `${averageOccupancy}%`, icon: Layers, color: '16, 185, 129' },
        { title: 'Storage Capacity Alerts', value: capacityAlertsCount.toString(), icon: AlertCircle, color: '239, 68, 68' }
      ]);
    } catch (err) {
      console.error('API Error loading WMS data:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSignOut = () => {
    localStorage.clear();
    setIsAuthenticated(false);
  };

  // Create Product handler
  const handleCreateProduct = async (e) => {
    e.preventDefault();
    setProdSuccess('');
    setProdError('');
    try {
      await api.post('/api/products', {
        name: prodName,
        sku: prodSku,
        description: prodDesc,
        price: parseFloat(prodPrice),
        weight: parseFloat(prodWeight)
      });
      setProdSuccess(`Product ${prodName} successfully created!`);
      setProdName('');
      setProdSku('');
      setProdDesc('');
      setProdPrice('');
      setProdWeight('');
      fetchWmsData();
    } catch (err) {
      setProdError(err.response?.data?.message || 'Error creating product.');
    }
  };

  // Create Warehouse handler
  const handleCreateWarehouse = async (e) => {
    e.preventDefault();
    setWhSuccess('');
    setWhError('');
    try {
      await api.post('/api/warehouses', {
        name: whName,
        location: whLoc,
        totalCapacity: parseInt(whCap)
      });
      setWhSuccess(`Warehouse ${whName} successfully registered!`);
      setWhName('');
      setWhLoc('');
      setWhCap('');
      fetchWmsData();
    } catch (err) {
      setWhError(err.response?.data?.message || 'Error registering warehouse.');
    }
  };

  // Create Bin handler
  const handleCreateBin = async (e) => {
    e.preventDefault();
    setBinSuccess('');
    setBinError('');
    try {
      await api.post('/api/bins', {
        binCode: binCodeVal,
        maxCapacity: parseInt(binMaxCap),
        warehouse: { id: parseInt(binWhId) }
      });
      setBinSuccess(`Storage Bin ${binCodeVal} successfully registered!`);
      setBinCodeVal('');
      setBinMaxCap('');
      setBinWhId('');
      fetchWmsData();
    } catch (err) {
      setBinError(err.response?.data?.message || 'Error registering storage bin.');
    }
  };

  // Create Order handler
  const handleCreateOrder = async (e) => {
    e.preventDefault();
    setOrderSuccess('');
    setOrderError('');
    try {
      await api.post('/api/orders', {
        orderNumber: orderNum,
        customerName: orderCust,
        orderItems: [
          {
            product: { id: parseInt(orderProdId) },
            quantity: parseInt(orderQty)
          }
        ]
      });
      setOrderSuccess(`Order ${orderNum} successfully placed!`);
      setOrderNum('');
      setOrderCust('');
      setOrderProdId('');
      setOrderQty('');
      fetchWmsData();
    } catch (err) {
      setOrderError(err.response?.data?.message || 'Error placing order.');
    }
  };

  const handleStaffRegister = async (e) => {
    e.preventDefault();
    setStaffSuccess('');
    setStaffError('');
    try {
      await api.post('/api/auth/register', {
        username: staffUsername,
        password: staffPassword,
        role: staffRole
      });
      setStaffSuccess(`Successfully registered staff member: ${staffUsername}`);
      setStaffUsername('');
      setStaffPassword('');
      setStaffRole('ROLE_OPERATOR');
    } catch (err) {
      setStaffError(err.response?.data?.message || 'Error registering new staff member.');
    }
  };

  const advanceOrderStatus = async (orderId, currentStatus) => {
    let nextStatus = '';
    if (currentStatus === 'PENDING') nextStatus = 'PICKING';
    else if (currentStatus === 'PICKING') nextStatus = 'PACKED';
    else if (currentStatus === 'PACKED') nextStatus = 'SHIPPED';

    if (!nextStatus) return;

    try {
      await api.put(`/api/orders/${orderId}/status?status=${nextStatus}`);
      fetchWmsData();
    } catch (err) {
      alert(err.response?.data?.message || 'Error executing transition');
    }
  };

  if (!isAuthenticated) {
    return <Login onLoginSuccess={() => setIsAuthenticated(true)} />;
  }

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Warehouse Overview</h1>
                <p style={{ color: 'var(--text-muted)' }}>Real-time database metrics loaded from PostgreSQL.</p>
              </div>
              <button onClick={fetchWmsData} style={{
                padding: '10px 16px', borderRadius: '12px', border: '1px solid var(--glass-border)',
                backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', cursor: 'pointer',
                display: 'flex', alignItems: 'center', gap: '8px', fontSize: '14px', fontWeight: '500'
              }}>
                <RefreshCw size={14} className={isLoading ? 'spin-anim' : ''} />
                Sync Database
              </button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '24px' }}>
              {stats.map((stat, i) => (
                <StatCard key={i} {...stat} />
              ))}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '2fr 1.2fr', gap: '24px' }}>
              <div className="glass-card">
                <h2 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '20px' }}>Active Orders Processing Queue</h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  {orders.filter(o => o.status !== 'SHIPPED').length === 0 ? (
                    <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>No active customer orders currently placed.</p>
                  ) : (
                    orders.filter(o => o.status !== 'SHIPPED').map(order => (
                      <div key={order.id} style={{
                        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                        padding: '16px', borderRadius: '12px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.01)'
                      }}>
                        <div>
                          <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                            <strong style={{ fontSize: '15px' }}>{order.orderNumber}</strong>
                            <span style={{
                              fontSize: '11px', fontWeight: '600', padding: '3px 8px', borderRadius: '20px',
                              backgroundColor: 'rgba(99, 102, 241, 0.1)', color: 'var(--accent-primary)'
                            }}>{order.status}</span>
                          </div>
                          <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Customer: {order.customerName}</span>
                          <div style={{ fontSize: '12px', marginTop: '6px', color: 'var(--text-muted)' }}>
                            Items: {order.orderItems?.map(item => `${item.product?.name} (x${item.quantity})`).join(', ')}
                          </div>
                        </div>
                        <button onClick={() => advanceOrderStatus(order.id, order.status)} style={{
                          padding: '8px 14px', borderRadius: '8px', border: 'none', cursor: 'pointer',
                          backgroundColor: 'var(--accent-primary)', color: 'var(--text-main)', fontSize: '13px', fontWeight: '600'
                        }}>
                          {order.status === 'PENDING' && 'Start Picking'}
                          {order.status === 'PICKING' && 'Mark Packed'}
                          {order.status === 'PACKED' && 'Ship Package'}
                        </button>
                      </div>
                    ))
                  )}
                </div>
              </div>

              <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                <div>
                  <h2 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '16px' }}>System Notice Board</h2>
                  <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start', padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(16, 185, 129, 0.05)', border: '1px solid rgba(16, 185, 129, 0.1)' }}>
                    <Info size={18} style={{ color: 'var(--success)', flexShrink: 0, marginTop: '2px' }} />
                    <p style={{ fontSize: '14px', color: 'var(--text-muted)' }}>
                      <strong style={{ color: 'var(--text-main)' }}>Full-Stack Wired:</strong> Live database connection established successfully over secure HTTP headers.
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
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            <div>
              <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Product Catalog</h1>
              <p style={{ color: 'var(--text-muted)' }}>Registered product dictionary fetched in real-time from PostgreSQL.</p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 2fr', gap: '32px', alignItems: 'start' }}>
              {/* Product Creation Form */}
              <div className="glass-card">
                <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '16px' }}>Register New Product</h3>
                <form onSubmit={handleCreateProduct} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  {prodSuccess && <div style={{ color: 'var(--success)', fontSize: '14px' }}>{prodSuccess}</div>}
                  {prodError && <div style={{ color: 'var(--danger)', fontSize: '14px' }}>{prodError}</div>}
                  
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>SKU Code</label>
                    <input type="text" value={prodSku} onChange={(e) => setProdSku(e.target.value)} required placeholder="e.g. SKU-BOX-100" style={{
                      padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                      backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '14px'
                    }} />
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Product Name</label>
                    <input type="text" value={prodName} onChange={(e) => setProdName(e.target.value)} required placeholder="e.g. Heavy Duty Pallet" style={{
                      padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                      backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '14px'
                    }} />
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Description</label>
                    <textarea value={prodDesc} onChange={(e) => setProdDesc(e.target.value)} placeholder="Enter details..." style={{
                      padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                      backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '14px', resize: 'none', height: '80px'
                    }} />
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Price ($)</label>
                      <input type="number" step="0.01" value={prodPrice} onChange={(e) => setProdPrice(e.target.value)} required placeholder="19.99" style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '14px'
                      }} />
                    </div>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Weight (kg)</label>
                      <input type="number" step="0.1" value={prodWeight} onChange={(e) => setProdWeight(e.target.value)} required placeholder="4.5" style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '14px'
                      }} />
                    </div>
                  </div>

                  <button type="submit" style={{
                    padding: '12px', borderRadius: '8px', border: 'none', cursor: 'pointer',
                    backgroundColor: 'var(--accent-primary)', color: 'var(--text-main)', fontWeight: '600', fontSize: '14px', marginTop: '6px'
                  }}>
                    Register Product
                  </button>
                </form>
              </div>

              {/* Product Catalog List */}
              <div className="glass-card">
                <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '16px' }}>Catalog Dictionary</h3>
                {products.length === 0 ? (
                  <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>No products registered yet.</p>
                ) : (
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: '16px' }}>
                    {products.map(product => (
                      <div key={product.id} style={{
                        padding: '16px', borderRadius: '12px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.01)', display: 'flex', flexDirection: 'column', gap: '6px'
                      }}>
                        <strong style={{ fontSize: '16px' }}>{product.name}</strong>
                        <span style={{ fontSize: '11px', color: 'var(--accent-primary)', fontWeight: '600' }}>SKU: {product.sku}</span>
                        <p style={{ fontSize: '13px', color: 'var(--text-muted)', minHeight: '36px', overflow: 'hidden' }}>{product.description || 'No description.'}</p>
                        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', borderTop: '1px solid var(--glass-border)', paddingTop: '8px', marginTop: '4px' }}>
                          <span>Price: <strong style={{ color: 'var(--success)' }}>${product.price}</strong></span>
                          <span>Weight: <strong>{product.weight} kg</strong></span>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        );
      case 'orders':
        return (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            <div>
              <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Fulfillment Manager</h1>
              <p style={{ color: 'var(--text-muted)' }}>Place new retail orders and check processing histories.</p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 2fr', gap: '32px', alignItems: 'start' }}>
              {/* Place Order Form */}
              <div className="glass-card">
                <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '16px' }}>Place New Order</h3>
                <form onSubmit={handleCreateOrder} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  {orderSuccess && <div style={{ color: 'var(--success)', fontSize: '14px' }}>{orderSuccess}</div>}
                  {orderError && <div style={{ color: 'var(--danger)', fontSize: '14px' }}>{orderError}</div>}

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Order Number</label>
                    <input type="text" value={orderNum} onChange={(e) => setOrderNum(e.target.value)} required placeholder="e.g. ORD-98765" style={{
                      padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                      backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '14px'
                    }} />
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Customer Name</label>
                    <input type="text" value={orderCust} onChange={(e) => setOrderCust(e.target.value)} required placeholder="e.g. Globex Corp" style={{
                      padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                      backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '14px'
                    }} />
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: '12px' }}>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Select SKU Product</label>
                      <select value={orderProdId} onChange={(e) => setOrderProdId(e.target.value)} required style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'var(--bg-secondary)', color: 'var(--text-main)', fontSize: '14px'
                      }}>
                        <option value="">-- Select SKU --</option>
                        {products.map(p => (
                          <option key={p.id} value={p.id}>{p.name} ({p.sku})</option>
                        ))}
                      </select>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Qty</label>
                      <input type="number" min="1" value={orderQty} onChange={(e) => setOrderQty(e.target.value)} required placeholder="1" style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '14px'
                      }} />
                    </div>
                  </div>

                  <button type="submit" style={{
                    padding: '12px', borderRadius: '8px', border: 'none', cursor: 'pointer',
                    backgroundColor: 'var(--accent-primary)', color: 'var(--text-main)', fontWeight: '600', fontSize: '14px', marginTop: '6px'
                  }}>
                    Place Order
                  </button>
                </form>
              </div>

              {/* Orders Ledger List */}
              <div className="glass-card">
                <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '16px' }}>Database Orders Registry</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  {orders.length === 0 ? (
                    <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>No orders currently registered.</p>
                  ) : (
                    orders.map(order => (
                      <div key={order.id} style={{
                        padding: '16px', borderRadius: '12px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.01)', display: 'flex', justifyContent: 'space-between', alignItems: 'center'
                      }}>
                        <div>
                          <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                            <strong style={{ fontSize: '15px' }}>{order.orderNumber}</strong>
                            <span style={{
                              fontSize: '11px', fontWeight: '600', padding: '3px 8px', borderRadius: '20px',
                              backgroundColor: order.status === 'SHIPPED' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(99, 102, 241, 0.1)',
                              color: order.status === 'SHIPPED' ? 'var(--success)' : 'var(--accent-primary)'
                            }}>{order.status}</span>
                          </div>
                          <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>Customer: {order.customerName}</span>
                          <div style={{ fontSize: '12px', marginTop: '6px', color: 'var(--text-muted)' }}>
                            Items: {order.orderItems?.map(item => `${item.product?.name} (x${item.quantity})`).join(', ')}
                          </div>
                        </div>
                        {order.status !== 'SHIPPED' && (
                          <button onClick={() => advanceOrderStatus(order.id, order.status)} style={{
                            padding: '8px 14px', borderRadius: '8px', border: 'none', cursor: 'pointer',
                            backgroundColor: 'var(--accent-primary)', color: 'var(--text-main)', fontSize: '13px', fontWeight: '600'
                          }}>
                            {order.status === 'PENDING' && 'Start Picking'}
                            {order.status === 'PICKING' && 'Mark Packed'}
                            {order.status === 'PACKED' && 'Ship Package'}
                          </button>
                        )}
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>
          </div>
        );
      case 'bins':
        return (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            <div>
              <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Bin & Space Manager</h1>
              <p style={{ color: 'var(--text-muted)' }}>Configure physical warehouses and register automated storage allocations.</p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 2fr', gap: '32px', alignItems: 'start' }}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                {/* Create Warehouse Form */}
                <div className="glass-card">
                  <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Building2 size={18} /> Register Warehouse
                  </h3>
                  <form onSubmit={handleCreateWarehouse} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    {whSuccess && <div style={{ color: 'var(--success)', fontSize: '13px' }}>{whSuccess}</div>}
                    {whError && <div style={{ color: 'var(--danger)', fontSize: '13px' }}>{whError}</div>}

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Warehouse Name</label>
                      <input type="text" value={whName} onChange={(e) => setWhName(e.target.value)} required placeholder="e.g. Apex Central" style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '13px'
                      }} />
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Location</label>
                      <input type="text" value={whLoc} onChange={(e) => setWhLoc(e.target.value)} required placeholder="e.g. Chicago Hub" style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '13px'
                      }} />
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Square Footage Capacity</label>
                      <input type="number" value={whCap} onChange={(e) => setWhCap(e.target.value)} required placeholder="e.g. 50000" style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '13px'
                      }} />
                    </div>

                    <button type="submit" style={{
                      padding: '10px', borderRadius: '8px', border: 'none', cursor: 'pointer',
                      backgroundColor: 'var(--accent-primary)', color: 'var(--text-main)', fontWeight: '600', fontSize: '13px', marginTop: '6px'
                    }}>
                      Create Warehouse
                    </button>
                  </form>
                </div>

                {/* Create Bin Form */}
                <div className="glass-card">
                  <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Layers size={18} /> Register Storage Bin
                  </h3>
                  <form onSubmit={handleCreateBin} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    {binSuccess && <div style={{ color: 'var(--success)', fontSize: '13px' }}>{binSuccess}</div>}
                    {binError && <div style={{ color: 'var(--danger)', fontSize: '13px' }}>{binError}</div>}

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Bin Code</label>
                      <input type="text" value={binCodeVal} onChange={(e) => setBinCodeVal(e.target.value)} required placeholder="e.g. BIN-A1-SEC3" style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '13px'
                      }} />
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Max Item Capacity</label>
                      <input type="number" min="1" value={binMaxCap} onChange={(e) => setBinMaxCap(e.target.value)} required placeholder="e.g. 10" style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', fontSize: '13px'
                      }} />
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                      <label style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Assign to Warehouse</label>
                      <select value={binWhId} onChange={(e) => setBinWhId(e.target.value)} required style={{
                        padding: '10px', borderRadius: '8px', border: '1px solid var(--glass-border)',
                        backgroundColor: 'var(--bg-secondary)', color: 'var(--text-main)', fontSize: '13px'
                      }}>
                        <option value="">-- Choose Warehouse --</option>
                        {warehouses.map(wh => (
                          <option key={wh.id} value={wh.id}>{wh.name} ({wh.location})</option>
                        ))}
                      </select>
                    </div>

                    <button type="submit" style={{
                      padding: '10px', borderRadius: '8px', border: 'none', cursor: 'pointer',
                      backgroundColor: 'var(--accent-primary)', color: 'var(--text-main)', fontWeight: '600', fontSize: '13px', marginTop: '6px'
                    }}>
                      Create Storage Bin
                    </button>
                  </form>
                </div>
              </div>

              {/* Warehouse & Storage Bins Layout Display */}
              <div className="glass-card">
                <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '20px' }}>Storage Floor Space Visualizer</h3>
                
                {warehouses.length === 0 ? (
                  <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>No warehouses currently registered.</p>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                    {warehouses.map(wh => {
                      const whBins = bins.filter(bin => bin.warehouse?.id === wh.id);
                      return (
                        <div key={wh.id} style={{
                          padding: '20px', borderRadius: '16px', border: '1px solid var(--glass-border)',
                          backgroundColor: 'rgba(255,255,255,0.02)', display: 'flex', flexDirection: 'column', gap: '16px'
                        }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div>
                              <h4 style={{ fontSize: '18px', fontWeight: '600', margin: 0 }}>{wh.name}</h4>
                              <span style={{ fontSize: '13px', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '4px', marginTop: '4px' }}>
                                <MapPin size={12} /> {wh.location} | Cap: {wh.totalCapacity} sqft
                              </span>
                            </div>
                            <span style={{
                              fontSize: '11px', fontWeight: '600', padding: '4px 10px', borderRadius: '20px',
                              backgroundColor: 'rgba(16, 185, 129, 0.1)', color: 'var(--success)'
                            }}>{whBins.length} Active Bins</span>
                          </div>

                          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: '14px' }}>
                            {whBins.length === 0 ? (
                              <p style={{ color: 'var(--text-muted)', fontSize: '13px', gridColumn: '1 / -1', margin: 0 }}>No storage bins registered in this warehouse yet.</p>
                            ) : (
                              whBins.map(bin => {
                                const occupancyPercentage = Math.round((bin.currentOccupancy / bin.maxCapacity) * 100);
                                const isFull = bin.currentOccupancy >= bin.maxCapacity;
                                return (
                                  <div key={bin.id} style={{
                                    padding: '12px 16px', borderRadius: '12px', border: '1px solid var(--glass-border)',
                                    backgroundColor: 'rgba(255,255,255,0.01)', display: 'flex', flexDirection: 'column', gap: '6px'
                                  }}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                      <strong style={{ fontSize: '14px', color: 'var(--text-main)' }}>{bin.binCode}</strong>
                                    </div>
                                    <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Occupancy: {bin.currentOccupancy} / {bin.maxCapacity}</span>
                                    <div style={{ width: '100%', height: '6px', borderRadius: '3px', backgroundColor: 'rgba(255,255,255,0.05)', overflow: 'hidden' }}>
                                      <div style={{
                                        width: `${Math.min(100, occupancyPercentage)}%`, height: '100%',
                                        backgroundColor: isFull ? 'var(--danger)' : 'var(--success)',
                                        transition: 'width 0.3s ease'
                                      }} />
                                    </div>
                                  </div>
                                );
                              })
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>
            </div>
          </div>
        );
      case 'users':
        return (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>
            <div>
              <h1 style={{ fontSize: '32px', fontWeight: '600', marginBottom: '8px' }}>Staff Registry</h1>
              <p style={{ color: 'var(--text-muted)' }}>Register new warehouse staff, operators, or administrators.</p>
            </div>

            <div className="glass-card" style={{ maxWidth: '500px' }}>
              <form onSubmit={handleStaffRegister} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '4px' }}>Create New Account</h3>

                {staffSuccess && (
                  <div style={{ padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(16, 185, 129, 0.05)', border: '1px solid rgba(16, 185, 129, 0.1)', fontSize: '14px', color: 'var(--success)' }}>
                    {staffSuccess}
                  </div>
                )}
                {staffError && (
                  <div style={{ padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(239, 68, 68, 0.05)', border: '1px solid rgba(239, 68, 68, 0.1)', fontSize: '14px', color: 'var(--danger)' }}>
                    {staffError}
                  </div>
                )}

                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  <label style={{ fontSize: '14px', color: 'var(--text-muted)' }}>Username</label>
                  <input type="text" value={staffUsername} onChange={(e) => setStaffUsername(e.target.value)} required style={{
                    padding: '12px', borderRadius: '12px', border: '1px solid var(--glass-border)',
                    backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', outline: 'none', fontSize: '15px'
                  }} />
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  <label style={{ fontSize: '14px', color: 'var(--text-muted)' }}>Temporary Password</label>
                  <input type="password" value={staffPassword} onChange={(e) => setStaffPassword(e.target.value)} required style={{
                    padding: '12px', borderRadius: '12px', border: '1px solid var(--glass-border)',
                    backgroundColor: 'rgba(255,255,255,0.02)', color: 'var(--text-main)', outline: 'none', fontSize: '15px'
                  }} />
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  <label style={{ fontSize: '14px', color: 'var(--text-muted)' }}>Assigned Role</label>
                  <select value={staffRole} onChange={(e) => setStaffRole(e.target.value)} style={{
                    padding: '12px', borderRadius: '12px', border: '1px solid var(--glass-border)',
                    backgroundColor: 'var(--bg-secondary)', color: 'var(--text-main)', outline: 'none', fontSize: '15px'
                  }}>
                    <option value="ROLE_OPERATOR">ROLE_OPERATOR (Daily Logistics & Shipping)</option>
                    <option value="ROLE_ADMIN">ROLE_ADMIN (Structural Controls)</option>
                  </select>
                </div>

                <button type="submit" style={{
                  padding: '14px', borderRadius: '12px', border: 'none', cursor: 'pointer',
                  background: 'linear-gradient(135deg, var(--accent-primary), var(--accent-secondary))',
                  color: 'var(--text-main)', fontSize: '16px', fontWeight: '600', marginTop: '10px'
                }}>
                  Register Staff Member
                </button>
              </form>
            </div>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div>
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} handleSignOut={handleSignOut} />
      <div style={{ marginLeft: '260px', padding: '40px', minHeight: '100vh' }}>
        {renderContent()}
      </div>
    </div>
  );
}