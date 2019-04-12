export interface IVet {
  id?: number;
  name?: string;
  address?: string;
  city?: string;
  stateProvince?: string;
  phone?: string;
}

export const defaultValue: Readonly<IVet> = {};
