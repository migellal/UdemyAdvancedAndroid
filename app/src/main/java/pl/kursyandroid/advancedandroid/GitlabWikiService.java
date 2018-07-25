package pl.kursyandroid.advancedandroid;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GitlabWikiService {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("{projectId}/wikis?with_content=1")
    Observable<List<Wiki>> getAll(@Header("PRIVATE-TOKEN") String token, @Path("projectId") int projectId);

    @GET("{projectId}/wikis/{slug}")
    Observable<Wiki> getOne(@Header("PRIVATE-TOKEN") String token, @Path("projectId") int projectId, @Path("slug") String slug);

    @POST("{projectId}/wikis")
    Observable<Wiki> createPage(@Header("PRIVATE-TOKEN") String token, @Path("projectId") int projectId, @Body Wiki wiki);

    @PUT("{projectId}/wikis/{slug}")
    Observable<Wiki> updatePage(@Header("PRIVATE-TOKEN") String token, @Path("projectId") int projectId, @Path("slug") String slug, @Body Wiki wiki);
}
