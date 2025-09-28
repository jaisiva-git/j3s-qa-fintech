import React, { useEffect, useState } from 'react'

const api = {
  async createUser(body) {
    const res = await fetch('/api/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
    return res.json()
  },
  async listUsers() {
    const res = await fetch('/api/users')
    return res.json()
  },
  async createTx(body) {
    const res = await fetch('/api/transactions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
    return res.json()
  },
  async listTx() {
    const res = await fetch('/api/transactions')
    return res.json()
  }
}

export default function App() {
  const [users, setUsers] = useState([])
  const [txs, setTxs] = useState([])
  const [uForm, setUForm] = useState({ name: '', email: '', accountType: 'standard' })
  const [tForm, setTForm] = useState({ userId: '', amount: 10, type: 'transfer', recipientId: '' })
  const [message, setMessage] = useState('')

  const refresh = async () => {
    const u = await api.listUsers()
    setUsers(u)
    const t = await api.listTx()
    setTxs(t)
  }

  useEffect(() => { refresh() }, [])

  const onCreateUser = async (e) => {
    e.preventDefault()
    const rsp = await api.createUser(uForm)
    setMessage(rsp.message || (rsp.success ? 'Created' : 'Error'))
    await refresh()
  }

  const onCreateTx = async (e) => {
    e.preventDefault()
    const rsp = await api.createTx(tForm)
    setMessage(rsp.message || (rsp.success ? 'Transfer ok' : 'Error'))
    await refresh()
  }

  return (
    <div style={{ fontFamily: 'sans-serif', padding: 24, maxWidth: 900, margin: '0 auto' }}>
      <h1 data-testid="app-title">Fintech Sample UI</h1>
      {message && <p aria-live="polite" data-testid="status-message">{message}</p>}

      <section style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
        <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
          <h2>Create User</h2>
          <form onSubmit={onCreateUser}>
            <label>Name<br/>
              <input data-testid="user-name" required value={uForm.name} onChange={e => setUForm({ ...uForm, name: e.target.value })} />
            </label><br/><br/>
            <label>Email<br/>
              <input data-testid="user-email" type="email" required value={uForm.email} onChange={e => setUForm({ ...uForm, email: e.target.value })} />
            </label><br/><br/>
            <label>Account Type<br/>
              <select data-testid="user-accountType" value={uForm.accountType} onChange={e => setUForm({ ...uForm, accountType: e.target.value })}>
                <option value="standard">standard</option>
                <option value="premium">premium</option>
              </select>
            </label><br/><br/>
            <button data-testid="btn-create-user" type="submit">Create User</button>
          </form>
        </div>

        <div style={{ border: '1px solid #ddd', padding: 16, borderRadius: 8 }}>
          <h2>Transfer</h2>
          <form onSubmit={onCreateTx}>
            <label>Sender (userId)<br/>
              <input data-testid="tx-userId" required value={tForm.userId} onChange={e => setTForm({ ...tForm, userId: e.target.value })} />
            </label><br/><br/>
            <label>Recipient (recipientId)<br/>
              <input data-testid="tx-recipientId" required value={tForm.recipientId} onChange={e => setTForm({ ...tForm, recipientId: e.target.value })} />
            </label><br/><br/>
            <label>Amount<br/>
              <input data-testid="tx-amount" type="number" step="0.01" min="0.01" required value={tForm.amount} onChange={e => setTForm({ ...tForm, amount: Number(e.target.value) })} />
            </label><br/><br/>
            <button data-testid="btn-create-tx" type="submit">Send</button>
          </form>
        </div>
      </section>

      <section style={{ marginTop: 32, display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
        <div>
          <h3>Users</h3>
          <table border="1" cellPadding="6" data-testid="table-users">
            <thead><tr><th>id</th><th>name</th><th>email</th><th>type</th><th>balance</th></tr></thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id}>
                  <td>{u.id}</td><td>{u.name}</td><td>{u.email}</td><td>{u.accountType}</td><td>{u.balance.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div>
          <h3>Transactions</h3>
          <table border="1" cellPadding="6" data-testid="table-transactions">
            <thead><tr><th>id</th><th>userId</th><th>recipientId</th><th>amount</th><th>type</th><th>createdAt</th></tr></thead>
            <tbody>
              {txs.map(t => (
                <tr key={t.id}>
                  <td>{t.id}</td><td>{t.userId}</td><td>{t.recipientId}</td><td>{t.amount}</td><td>{t.type}</td><td>{t.createdAt}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  )
}