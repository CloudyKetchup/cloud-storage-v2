export interface WithId
{
  id : string
}

interface DirectoryItem extends WithId
{
  name        : string,
  path        : string,
  folder      : string,
  size        : number,
  dateCreated : string,
}

export interface Folder extends DirectoryItem
{
  root: boolean
};

export interface File extends DirectoryItem
{
  extension: string
};