﻿using Racing2D.Services.Races;
using System.Collections.Generic;

namespace Racing2D.Services
{
    public class RacesService
    {
        public List<RaceInfo>  GetRaces()
        {
            return new List<RaceInfo>
            {
                new RaceInfo {Id =1, Creator="lando",  Name="round" },
                new RaceInfo {Id = 2, Creator="Jaron", Name="square" }
            };
        }

        public Track GetRaceTrack(int id)
        {
            return new Track
            {
                Tiles = Dummy1(),
                StartPosition = new StartPosition { X = 1, Y = 1 }
            };
        }

        private static TileType[][] Dummy1()
        {
            return new TileType[][]
            {
                new TileType[] { TileType.SouthEast, TileType.SouthWest, TileType.NoRoads, TileType.NoRoads },
                new TileType[] { TileType.NorthEast, TileType.Crossroads, TileType.EastWest, TileType.SouthWest},
                new TileType[] { TileType.NoRoads, TileType.NorthEast, TileType.EastWest, TileType.NorthWest},
            };
        }

        private static TileType[][] Dummy2()
        {
            return new TileType[][]
            {
                new TileType[] { TileType.SouthEast, TileType.EastWest, TileType.SouthWest },
                new TileType[] { TileType.NorthEast, TileType.EastWest, TileType.NorthWest},
            };
        }
    }
}
