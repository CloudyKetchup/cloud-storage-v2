import { API_URL } from "./env.config";

import { StorageStats } from "../models/StorageStats";

import axios from "axios";

class FolderClient
{
  private static inst: FolderClient | undefined; // client instance
  private URL = `${API_URL}/folder`;

  private constructor() {}

  static instance = () : FolderClient =>
  {
    if (!FolderClient.inst)
    {
      FolderClient.inst = new FolderClient();
    }
    return FolderClient.inst;
  }

  getRootStats = (onError?: () => void) : Promise<StorageStats | null> =>
  (
    axios.get(`${this.URL}/root/stats`)
      .then(response => response.data)
      .catch(onError)
  );
}

export default FolderClient;