using Racing2D.Services;
using Racing2D.Services.Races;
using System;
using System.Collections.Generic;
using System.Web.Http;

namespace Racing2D.Backend.Controllers
{
    [RoutePrefix("races")]
    public class RaceOverviewController : ApiController
    {
        private RacesService _racesService = new RacesService();

        [HttpGet()]
        [Route("")]
        public List<RaceInfo> GetRaces()
        {
            return _racesService.GetRaces();
        }

        [HttpGet()]
        [Route("track")]
        public Track GetRaceTrack()
        {
            return _racesService.GetRaceTrack(0);
        }

        [HttpPost()]
        [Route("track")]
        public void SaveRaceTrack(Track track)
        {
            _racesService.SaveRaceTrack(track);
        }
    }
}
