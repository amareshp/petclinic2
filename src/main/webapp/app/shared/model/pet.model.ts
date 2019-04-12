import { IOwner } from 'app/shared/model/owner.model';

export interface IPet {
  id?: number;
  name?: string;
  type?: string;
  breed?: string;
  owner?: IOwner;
}

export const defaultValue: Readonly<IPet> = {};
