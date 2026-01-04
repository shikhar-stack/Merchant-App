'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';

export default function MerchantDashboard() {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState<any[]>([]);
    const [loading, setLoading] = useState(false);

    const searchCrops = async (q: string) => {
        setLoading(true);
        try {
            // Calls ElasticSearch endpoint
            const res = await fetch(`http://localhost:8080/api/crops/search?query=${q}`);
            const data = await res.json();
            setResults(data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        searchCrops('');
    }, []);

    const handleOrder = async (cropId: string, quantity: number) => {
        // TODO: Implement order modal or direct call
        const quantityToBuy = prompt("Enter quantity (Kg):");
        if (!quantityToBuy) return;

        const user = JSON.parse(localStorage.getItem('user') || '{}');

        try {
            const res = await fetch(`http://localhost:8080/api/orders?cropId=${cropId}&quantity=${quantityToBuy}`, {
                method: 'POST',
                headers: {
                    'Authorization': 'Basic ' + btoa(user.email + ":password")
                },
            });
            if (res.ok) alert("Order Placed!");
            else alert("Order Failed");
        } catch (e) {
            alert("Error");
        }
    };

    return (
        <div className="container" style={{ padding: '2rem' }}>
            <h1>Merchant Dashboard</h1>
            <p style={{ color: '#888', marginBottom: '2rem' }}>Find and buy fresh crops</p>

            <div style={{ marginBottom: '2rem' }}>
                <input
                    className="input"
                    placeholder="Search crops via ElasticSearch..."
                    value={query}
                    onChange={(e) => {
                        setQuery(e.target.value);
                        searchCrops(e.target.value);
                    }}
                    style={{ fontSize: '1.2rem', padding: '1rem' }}
                />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '1.5rem' }}>
                {loading ? <p>Loading...</p> : results.map((crop) => (
                    <div key={crop.id} className="card" style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                        <h3 style={{ color: 'var(--primary)' }}>{crop.name}</h3>
                        <p><strong>Price:</strong> ${crop.pricePerKg}/kg</p>
                        <p><strong>Available:</strong> {crop.quantityAvailable} kg</p>
                        <p style={{ fontSize: '0.9rem', color: '#888' }}>Farmer: {crop.farmerName || 'Unknown'}</p>

                        <div style={{ marginTop: 'auto', display: 'flex', gap: '0.5rem' }}>
                            <button className="btn btn-primary" style={{ flex: 1 }} onClick={() => handleOrder(crop.id, crop.quantityAvailable)}>
                                Order
                            </button>
                            <Link href={`/chat?with=${crop.farmerName || 'farmer'}`} className="btn" style={{ background: '#333', color: 'white' }}>
                                Chat
                            </Link>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
