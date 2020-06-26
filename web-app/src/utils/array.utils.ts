import { WithId } from "../models/Directory";

export function findById<T extends WithId>(array: T[], id: string) : T | undefined
{
  return array.find(item => item.id === id)
};

export function deleteItem<T>(array : T[], item: T)
{
  const index = array.indexOf(item);

  if (index !== -1) array.splice(index, 1);
}

export function deleteById<T extends WithId>(array: T[], id: string)
{
  const index = array.findIndex(item => item.id === id)

  if (index !== -1) array.splice(index, 1);
}

export function isLast<T>(array: T[], item: T): boolean
{
  const last = array[array.length - 1];

  return last === item;
}

export function isLastById<T extends WithId>(array: T[], item: T): boolean
{
  const last = array[array.length - 1];

  return item.id === last.id;
}