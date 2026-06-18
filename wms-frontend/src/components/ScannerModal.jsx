import React, { useEffect } from 'react';
import { Html5Qrcode } from 'html5-qrcode';
import { X } from 'lucide-react';

export default function ScannerModal({ onClose, onScanSuccess }) {
  useEffect(() => {
    const html5Qrcode = new Html5Qrcode("scanner-target");
    
    const config = { fps: 10, qrbox: { width: 250, height: 250 } };
    
    // Start camera stream
    html5Qrcode.start(
      { facingMode: "environment" },
      config,
      (decodedText) => {
        // Stop scanner on success
        html5Qrcode.stop().then(() => {
          onScanSuccess(decodedText);
        }).catch(err => {
          console.error("Error stopping scanner", err);
          onScanSuccess(decodedText); // Proceed anyway
        });
      },
      (errorMessage) => {
        // Silent validation error logs (fires during frame search)
      }
    ).catch(err => {
      console.error("Error starting camera", err);
    });

    return () => {
      // Cleanup: stop scanning if modal unmounts
      if (html5Qrcode.isScanning) {
        html5Qrcode.stop().catch(err => console.error("Cleanup stop error", err));
      }
    };
  }, [onScanSuccess]);

  return (
    <div style={{
      position: 'fixed', left: 0, top: 0, width: '100vw', height: '100vh',
      backgroundColor: 'rgba(2, 6, 23, 0.85)', backdropFilter: 'blur(8px)',
      display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1100
    }}>
      <div className="glass-card" style={{ width: '450px', textAlign: 'center', position: 'relative' }}>
        <button onClick={onClose} style={{
          position: 'absolute', right: '16px', top: '16px', backgroundColor: 'transparent',
          border: 'none', color: 'var(--text-main)', cursor: 'pointer', outline: 'none'
        }}>
          <X size={20} />
        </button>

        <h3 style={{ fontSize: '18px', fontWeight: '600', marginBottom: '8px' }}>Scan Barcode / QR Label</h3>
        <p style={{ color: 'var(--text-muted)', fontSize: '14px', marginBottom: '20px' }}>Hold the printed label up to your camera</p>

        <div style={{
          width: '100%', height: '300px', backgroundColor: 'black', borderRadius: '12px',
          overflow: 'hidden', position: 'relative', border: '1px solid var(--glass-border)'
        }}>
          <div id="scanner-target" style={{ width: '100%', height: '100%' }}></div>
        </div>

        <button onClick={onClose} style={{
          width: '100%', padding: '12px', borderRadius: '8px', border: '1px solid var(--glass-border)',
          backgroundColor: 'transparent', color: 'var(--text-main)', cursor: 'pointer', marginTop: '20px',
          fontWeight: '500'
        }}>
          Cancel Scan
        </button>
      </div>
    </div>
  );
}
