export interface IOwner {
  id?: number;
  name?: string;
  email?: string;
  phone?: string;
}

export const defaultValue: Readonly<IOwner> = {};
