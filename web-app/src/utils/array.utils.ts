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