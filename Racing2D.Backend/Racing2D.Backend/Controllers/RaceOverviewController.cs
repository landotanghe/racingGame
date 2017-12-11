using System.Web.Http;

namespace Racing2D.Backend.Controllers
{
    [RoutePrefix("races")]
    public class RaceOverviewController : ApiController
    {
        [HttpGet()]
        [Route("")]
        public string GetRaces()
        {
            return "hello";
        } 
    }
}
