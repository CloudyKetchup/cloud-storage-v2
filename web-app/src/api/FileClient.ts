import { API_URL } from "./env.config";

import axios from "axios";

import { File }					from "../models/Directory";
import { ApiResponse }	from "../models/ApiResponse";

class FileClient
{
	private static inst: FileClient | undefined; // client instance
  private URL = `${API_URL}/file`;

  private constructor() {}

  static instance = () : FileClient =>
  {
    if (!FileClient.inst)
    {
      FileClient.inst = new FileClient();
    }
    return FileClient.inst;
  }

  upload = (formData: FormData, folderId: string) : Promise<ApiResponse<File>> =>
  (
  	axios.post(`${this.URL}/upload?folderId=${folderId}`, formData)
  		.then(response => ({ data : response.data }))
  		.catch(e => ({ error : e }))
  );
}

export default FileClient;