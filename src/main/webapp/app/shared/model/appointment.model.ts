import { Moment } from 'moment';
import { ISlot } from 'app/shared/model/slot.model';
import { IVet } from 'app/shared/model/vet.model';
import { IPet } from 'app/shared/model/pet.model';

export interface IAppointment {
  id?: number;
  apptTime?: Moment;
  slot?: ISlot;
  vet?: IVet;
  pet?: IPet;
}

export const defaultValue: Readonly<IAppointment> = {};
