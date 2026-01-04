import './globals.css'
import type { Metadata } from 'next'

export const metadata: Metadata = {
    title: 'Merchant App',
    description: 'Connecting Farmers and Merchants',
}

export default function RootLayout({
    children,
}: {
    children: React.ReactNode
}) {
    return (
        <html lang="en">
            <body>{children}</body>
        </html>
    )
}
