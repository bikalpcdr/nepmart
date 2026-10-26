/**
 * Shared styles for admin CRUD pages (headings, toolbars, tables, badges,
 * forms). Kept in one place so the four admin pages stay visually
 * consistent without a global stylesheet (the codebase styles every
 * component itself).
 */
export const ADMIN_STYLES = `
  .page-head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
    flex-wrap: wrap;
    margin-bottom: 16px;
  }
  .page-head h2 {
    margin: 0;
    font-size: 22px;
    color: #1f1716;
  }
  .page-head p {
    margin: 4px 0 0;
    color: #625655;
    font-size: 14px;
  }

  .toolbar {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    align-items: center;
    margin-bottom: 16px;
  }
  .toolbar input,
  .toolbar select {
    padding: 9px 14px;
    border: 1.5px solid #d7d2d2;
    border-radius: 999px;
    font-size: 14px;
    background: #fff;
    color: #1f1716;
    outline: none;
  }
  .toolbar input:focus,
  .toolbar select:focus {
    border-color: #cc0b1d;
  }
  .toolbar input {
    min-width: 240px;
  }

  .table-wrap {
    border: 1px solid rgba(108, 106, 106, 0.12);
    border-radius: 16px;
    overflow: hidden;
    background: #fff;
  }
  table {
    width: 100%;
    border-collapse: collapse;
    font-size: 14px;
  }
  th,
  td {
    text-align: left;
    padding: 12px 16px;
    border-bottom: 1px solid rgba(108, 106, 106, 0.1);
    vertical-align: middle;
  }
  th {
    background: #faf8f8;
    color: #625655;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    white-space: nowrap;
  }
  tr:last-child td {
    border-bottom: none;
  }
  td.strong {
    font-weight: 600;
    color: #1f1716;
  }
  td.num,
  th.num {
    text-align: right;
    white-space: nowrap;
  }
  .actions {
    display: inline-flex;
    gap: 12px;
    justify-content: flex-end;
  }
  .state {
    text-align: center;
    padding: 32px 16px;
    color: #625655;
  }

  .badge {
    display: inline-block;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 700;
    background: #f1eded;
    color: #625655;
    white-space: nowrap;
  }
  .badge.ok {
    background: rgba(22, 163, 74, 0.12);
    color: #15803d;
  }
  .badge.warn {
    background: rgba(217, 119, 6, 0.14);
    color: #b45309;
  }
  .badge.danger {
    background: rgba(220, 38, 38, 0.12);
    color: #b91c1c;
  }
  .badge.brand {
    background: rgba(204, 11, 29, 0.1);
    color: #cc0b1d;
  }

  .btn {
    padding: 9px 18px;
    border-radius: 999px;
    border: 1.5px solid transparent;
    font-size: 14px;
    font-weight: 700;
    cursor: pointer;
    background: #fff;
    color: #1f1716;
    transition: opacity 0.15s ease;
  }
  .btn:disabled {
    opacity: 0.5;
    cursor: default;
  }
  .btn.primary {
    background: #cc0b1d;
    color: #fff;
  }
  .btn.primary:hover:not(:disabled) {
    opacity: 0.9;
  }
  .btn.ghost {
    border-color: #d7d2d2;
  }
  .btn.ghost:hover:not(:disabled) {
    border-color: #cc0b1d;
    color: #cc0b1d;
  }
  .btn.danger {
    background: #b91c1c;
    color: #fff;
  }

  button.link {
    background: none;
    border: none;
    padding: 0;
    font: inherit;
    font-weight: 700;
    color: #cc0b1d;
    cursor: pointer;
  }
  button.link:hover {
    text-decoration: underline;
  }
  button.link.danger {
    color: #b91c1c;
  }
  button.link.muted {
    color: #625655;
  }

  .field {
    margin-bottom: 14px;
  }
  .field label {
    display: block;
    font-size: 13px;
    font-weight: 700;
    color: #1f1716;
    margin-bottom: 6px;
  }
  .field label .hint {
    font-weight: 400;
    color: #625655;
  }
  .field input,
  .field select {
    width: 100%;
    padding: 10px 14px;
    border: 1.5px solid #d7d2d2;
    border-radius: 10px;
    font-size: 14px;
    outline: none;
    background: #fff;
    color: #1f1716;
    box-sizing: border-box;
  }
  .field input:focus,
  .field select:focus {
    border-color: #cc0b1d;
  }
  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 20px;
  }

  .muted {
    color: #625655;
    font-size: 14px;
  }

  .chip-list {
    list-style: none;
    margin: 0 0 16px;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  .chip-list li {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 10px 14px;
    border: 1px solid rgba(108, 106, 106, 0.14);
    border-radius: 10px;
    background: #faf8f8;
  }
  .chip {
    display: flex;
    flex-direction: column;
    gap: 2px;
    font-weight: 700;
    font-size: 14px;
    color: #1f1716;
  }
  .chip-sub {
    font-weight: 400;
    font-size: 12px;
    color: #625655;
  }
  .assign-row {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    align-items: center;
    padding-top: 4px;
    border-top: 1px dashed rgba(108, 106, 106, 0.2);
  }
  .assign-row select {
    flex: 1;
    min-width: 180px;
    padding: 9px 14px;
    border: 1.5px solid #d7d2d2;
    border-radius: 10px;
    font-size: 14px;
    outline: none;
    background: #fff;
  }
  .assign-row select:focus {
    border-color: #cc0b1d;
  }

  @media (max-width: 720px) {
    .table-wrap {
      overflow-x: auto;
    }
    th,
    td {
      padding: 10px 12px;
    }
  }
`;
