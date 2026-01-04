import Link from 'next/link'

export default function Home() {
    return (
        <main style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
            <h1 style={{ fontSize: '4rem', fontWeight: '800', background: 'linear-gradient(to right, #fff, #888)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent', marginBottom: '1rem' }}>
                Merchant App
            </h1>
            <p style={{ fontSize: '1.2rem', color: '#888', marginBottom: '3rem' }}>
                Empowering specific Farmers and Merchants directly.
            </p>

            <div style={{ display: 'flex', gap: '2rem' }}>
                <Link href="/auth/login" className="btn btn-primary">
                    Login
                </Link>
                <Link href="/auth/register" className="btn" style={{ background: '#333', color: 'white' }}>
                    Get Started
                </Link>
            </div>
        </main>
    )
}
