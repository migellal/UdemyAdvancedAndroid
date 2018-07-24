package pl.kursyandroid.advancedandroid;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface GitlabWikiService {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("{projectId}/wikis?with_content=1")
    Observable<List<Wiki>> getAll(@Header("PRIVATE-TOKEN") String token, @Path("projectId") int projectId);
}
