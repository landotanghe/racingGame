using Racing2D.Services;
using Racing2D.Services.Races;
using System.Collections.Generic;
using System.Threading.Tasks;
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
        [Route("{id}")]
        public Track GetRaceTrack(string id)
        {
            return _racesService.GetRaceTrack(id);
        }

        [HttpPost()]
        [Route("")]
        public async Task SaveRaceTrack(Track track)
        {
            await _racesService.SaveRaceTrack(track);
        }
    }
}
