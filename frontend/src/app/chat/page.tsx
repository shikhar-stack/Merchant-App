'use client';

import { useState, useEffect, useRef } from 'react';
import { useSearchParams } from 'next/navigation';
// import { Client } from '@stomp/stompjs'; // Uncomment when installed
// import SockJS from 'sockjs-client';

export default function ChatPage() {
    const searchParams = useSearchParams();
    const receiverEmail = searchParams.get('with') || '';
    const [messages, setMessages] = useState<any[]>([]);
    const [input, setInput] = useState('');
    const clientRef = useRef<any>(null); // Stomp Client
    const user = typeof window !== 'undefined' ? JSON.parse(localStorage.getItem('user') || '{}') : {};

    useEffect(() => {
        if (!user.email) return;

        // Load history
        fetch(`http://localhost:8080/api/messages?sender=${user.email}&receiver=${receiverEmail}`)
            .then(res => res.json())
            .then(data => setMessages(data))
            .catch(err => console.error(err));

        // Initialize WebSocket (Conceptually)
        // const socket = new SockJS('http://localhost:8080/ws');
        // const client = new Client({
        //   webSocketFactory: () => socket,
        //   onConnect: () => {
        //      client.subscribe('/user/' + user.email + '/queue/messages', (msg) => {
        //          const body = JSON.parse(msg.body);
        //          setMessages(prev => [...prev, body]);
        //      });
        //   }
        // });
        // client.activate();
        // clientRef.current = client;

        // return () => client.deactivate();
    }, [receiverEmail, user.email]);

    const sendMessage = () => {
        if (!input || !clientRef.current) return;

        const msg = {
            senderEmail: user.email,
            receiverEmail: receiverEmail,
            content: input,
            timestamp: new Date().toISOString() // Backend will override
        };

        // clientRef.current.publish({ destination: '/app/chat', body: JSON.stringify(msg) });

        // Optimistic update
        setMessages(prev => [...prev, msg]);
        setInput('');
    };

    return (
        <div className="container" style={{ padding: '2rem', height: '100vh', display: 'flex', flexDirection: 'column' }}>
            <h1>Chat with {receiverEmail}</h1>

            <div className="card" style={{ flex: 1, margin: '1rem 0', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {messages.map((m, i) => (
                    <div key={i} style={{
                        alignSelf: m.senderEmail === user.email ? 'flex-end' : 'flex-start',
                        background: m.senderEmail === user.email ? 'var(--primary)' : '#333',
                        padding: '0.8rem 1.2rem',
                        borderRadius: '20px',
                        maxWidth: '70%'
                    }}>
                        <p>{m.content}</p>
                        <span style={{ fontSize: '0.7rem', opacity: 0.7 }}>{new Date(m.timestamp).toLocaleTimeString()}</span>
                    </div>
                ))}
                {messages.length === 0 && <p style={{ textAlign: 'center', color: '#666' }}>No messages yet.</p>}
            </div>

            <div style={{ display: 'flex', gap: '1rem' }}>
                <input
                    className="input"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    placeholder="Type a message..."
                    style={{ marginBottom: 0 }}
                    onKeyDown={e => e.key === 'Enter' && sendMessage()}
                />
                <button className="btn btn-primary" onClick={sendMessage}>Send</button>
            </div>
        </div>
    );
}
