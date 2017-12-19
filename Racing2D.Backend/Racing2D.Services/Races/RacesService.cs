using Racing2D.Services.Races;
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

        public TileType[][] GetRaceTrack(int id)
        {
            return new TileType[][]
            {
                new TileType[] { TileType.NorthEast, TileType.EastWest, TileType.NorthWest},
                new TileType[] { TileType.SouthEast, TileType.EastWest, TileType.SouthWest },
            };
        }
    }
}
