﻿using Racing2D.Services;
using Racing2D.Services.Races;
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
    }
}
