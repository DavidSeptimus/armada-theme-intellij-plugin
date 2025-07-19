// @ts-ignore
import React, { useState } from 'react';
import './Modal.css';

interface ModalProps {
  title: string;
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
}

interface UserDetails {
  name: string;
  email: string;
  role: string;
}

const UserDetailsModal: React.FC<ModalProps> = ({ title, isOpen, onClose, onConfirm }) => {
  const [userDetails] = useState<UserDetails>({
    name: 'John Doe',
    email: 'john.doe@example.com',
    role: 'Developer'
  });

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <div className="modal-header">
          <h2>{title}</h2>
          <button className="close-button" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          <div className="user-details">
            <div className="detail-row">
              <span className="label">Name:</span>
              <span className="value">{userDetails.name}</span>
            </div>
            <div className="detail-row">
              <span className="label">Email:</span>
              <span className="value">{userDetails.email}</span>
            </div>
            <div className="detail-row">
              <span className="label">Role:</span>
              <span className="value">{userDetails.role}</span>
            </div>
          </div>
        </div>

        <div className="modal-footer">
          <button className="cancel-button" onClick={onClose}>Cancel</button>
          <button className="confirm-button" onClick={onConfirm}>Confirm</button>
        </div>
      </div>
    </div>
  );
};

export default UserDetailsModal;