'use client';

import { useState, useEffect } from 'react';

export default function FarmerDashboard() {
    const [crops, setCrops] = useState<any[]>([]);
    const [newCrop, setNewCrop] = useState({ name: '', pricePerKg: '', quantityAvailable: '' });
    const [msg, setMsg] = useState('');

    useEffect(() => {
        // In v1, we rely on stored user. Real app uses proper auth context/hooks.
        const userStr = localStorage.getItem('user');
        if (!userStr) window.location.href = '/auth/login';
        // fetchCrops(); // Need endpoint for 'my crops'
    }, []);

    const handleAddCrop = async (e: React.FormEvent) => {
        e.preventDefault();
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        if (!user.email) return;

        try {
            const res = await fetch('http://localhost:8080/api/crops', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Basic ' + btoa(user.email + ":password") // Mock Auth for V1
                },
                body: JSON.stringify({
                    name: newCrop.name,
                    pricePerKg: parseFloat(newCrop.pricePerKg),
                    quantityAvailable: parseFloat(newCrop.quantityAvailable)
                })
            });

            if (res.ok) {
                setMsg('Crop added successfully!');
                setNewCrop({ name: '', pricePerKg: '', quantityAvailable: '' });
            } else {
                setMsg('Failed to add crop');
            }
        } catch (err) {
            setMsg('Error adding crop');
        }
    };

    return (
        <div className="container" style={{ padding: '2rem' }}>
            <h1>Farmer Dashboard</h1>
            <p style={{ color: '#888', marginBottom: '2rem' }}>Manage your crops and orders</p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' }}>
                <div className="card">
                    <h3>Add New Crop</h3>
                    {msg && <p style={{ color: 'var(--success)', margin: '1rem 0' }}>{msg}</p>}
                    <form onSubmit={handleAddCrop}>
                        <input className="input" placeholder="Crop Name" value={newCrop.name} onChange={e => setNewCrop({ ...newCrop, name: e.target.value })} required />
                        <input className="input" type="number" placeholder="Price per Kg" value={newCrop.pricePerKg} onChange={e => setNewCrop({ ...newCrop, pricePerKg: e.target.value })} required />
                        <input className="input" type="number" placeholder="Quantity (Kg)" value={newCrop.quantityAvailable} onChange={e => setNewCrop({ ...newCrop, quantityAvailable: e.target.value })} required />
                        <button className="btn btn-primary" style={{ width: '100%' }}>Add Crop</button>
                    </form>
                </div>

                <div className="card">
                    <h3>Your Crops</h3>
                    <p style={{ color: '#666', marginTop: '1rem' }}>List of your active listings will appear here.</p>
                    {/* List would go here */}
                </div>
            </div>
        </div>
    );
}
