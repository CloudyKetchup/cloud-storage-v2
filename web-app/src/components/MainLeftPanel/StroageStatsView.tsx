import React, { FC, useState, useEffect } from "react";

import { CompactSeparator } from "../CompactSeparator/CompactSeparator";

import { Theme } from "../../context/ThemeContext";

import { PropsWithTheme } from "../../models/PropsWithTheme";
import { StorageStats }   from "../../models/StorageStats";

import FolderClient from "../../api/FolderClient";

const StorageStatsView: FC<PropsWithTheme> = ({ theme }) =>
{
  const [stats, setStats] = useState<StorageStats>();
  const [error, setError] = useState<boolean>(false);
  const client = FolderClient.instance();

  useEffect(() =>
  {
    const fetchStats = async () =>
    {
      const { data, error } = await client.getRootStats();

      data ? setStats(data) : setError(error !== null);
    };

    fetchStats();
  }, [])

  const calcPercent = () : number =>
  {
    if (stats)
    {
      const { total, free } = stats;

      return (total - free) / total * 100
    }
    return 0;
  };

  const style = { background: theme == Theme.DARK ? "#4a4a4a" : "" };

  return (
    <div className={`storage-stats-view`}>
      <CompactSeparator/>
      <div>
        <div>
          <span>{stats?.used || 0} / {stats?.total || 0} GB</span>
        </div>
        <div>
          <div className="storage-stats-memory-bar" style={style}>
            <div style={{ width: `${calcPercent()}%` }}/>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StorageStatsView;