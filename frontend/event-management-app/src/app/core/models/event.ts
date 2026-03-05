import { User } from "./user";

export enum EventType {
  SOCIAL = 'SOCIAL',
  EDUCATIONAL = 'EDUCATIONAL',
  CULTURAL = 'CULTURAL',
  NETWORKING = 'NETWORKING',
  WORKSHOP = 'WORKSHOP',
  CONFERENCE = 'CONFERENCE',
  CITY = 'CITY',
  UNIVERSITY = 'UNIVERSITY',
  SPORTS = 'SPORTS',
  PARTY = 'PARTY',
  ACADEMIC = 'ACADEMIC',
}

export enum EventStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  AVAILABLE = 'AVAILABLE',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED',
  BOOKED =  'BOOKED'
}

export enum ParticipationStatus {
  GOING = 'GOING',
  INTERESTED = 'INTERESTED',
  DECLINED = 'DECLINED'
}

export interface Event {
  id: number,
  name: string,
  description: string,
  imageUrl: string,
  startTime: string,
  endTime: string,
  room: string,
  capacity: number,
  locationId: number,
  location: string,
  locationCapacity: number,
  longitude: number,
  latitude: number,
  going: number,
  interested: number,
  declined: number,
  status: EventStatus,
  price: number,
  type: EventType,
  organizer: string,
  creator: User,
  userParticipationStatus: ParticipationStatus
  numParticipants: number;
  averageRating: number;
  comments: Comment[];
}

export interface Comment {
  id: number;
  text: string;
  username: string;
}
