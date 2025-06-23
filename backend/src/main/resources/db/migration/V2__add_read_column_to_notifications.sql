-- Add 'read' column to notifications table
ALTER TABLE notifications ADD COLUMN `read` BIT(1) NOT NULL DEFAULT b'0';
