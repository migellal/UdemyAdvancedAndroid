package pl.kursyandroid.advancedandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GitlabWikiActivity extends AppCompatActivity {

    private static final String TAG = "RX_RETROFIT";
    private int projectId = 4749359;
    private List<String> slugList;
    private GitlabWikiService gitlab;
    @BindView(R.id.wikiPageList)
    ListView wikiPageList;
    @BindView(R.id.slugGetB)
    Button slugGetContent;
    @BindView(R.id.slugGetET)
    EditText slugGetName;
    @BindView(R.id.wikiPageTitleGet)
    TextView wikiPageTitle;
    @BindView(R.id.wikiPageContentGet)
    TextView wikiPageContent;
    @BindView(R.id.wikiPageSlugGet)
    TextView wikiPageSlug;
    @BindView(R.id.createOrUpdatePost)
    Button createOrUpdate;
    @BindView(R.id.titleSlugPost)
    EditText titleSlug;
    @BindView(R.id.contentPost)
    EditText content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gitlab_wiki);
        ButterKnife.bind(this);
        initializeGitlabWikiService();
        getAll();

        slugGetContent.setOnClickListener(
                val -> {
                    gitlab.getOne(TopSecret.API_KEY, projectId, slugGetName.getText().toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                    v -> {
                                        wikiPageTitle.setText(v.getTitle());
                                        wikiPageContent.setText(v.getContent());
                                        wikiPageSlug.setText(v.getSlug());
                                    },
                                    error -> {
                                        Log.e(TAG, error.getMessage(), error);
                                        Toast.makeText(this, "Prawdopodbnie błędna nazwa strony wiki: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                            );
                }
        );

        createOrUpdate.setOnClickListener(
                val -> {
                    String slug = titleSlug.getText().toString();
                    Wiki wiki = new Wiki();
                    wiki.setTitle(slug);
                    wiki.setContent(content.getText().toString());
                    wiki.setSlug(slug);
                    if(slugList.contains(slug)) {
                        gitlab.updatePage(TopSecret.API_KEY, projectId, slug, wiki)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        v -> {
                                            Toast.makeText(this, "update: " + v.getSlug(), Toast.LENGTH_LONG).show();
                                        },
                                        error -> {
                                            Log.e(TAG, error.getMessage(), error);
                                            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                                        },
                                        () -> {
                                            getAll();
                                        }
                                );
                    } else {
                        gitlab.createPage(TopSecret.API_KEY, projectId, wiki)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        v -> {
                                            Toast.makeText(this, "create: " + v.getSlug(), Toast.LENGTH_LONG).show();
                                        },
                                        error -> {
                                            Log.e(TAG, error.getMessage(), error);
                                            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                                        },
                                        () -> {
                                            getAll();
                                        }
                                );
                    }
                }
        );
    }

    private void initializeGitlabWikiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gitlab.com/api/v4/projects/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        gitlab = retrofit.create(GitlabWikiService.class);
    }

    private void getAll() {
        slugList = new ArrayList<>();
        gitlab.getAll(TopSecret.API_KEY, projectId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        val -> {
                            for (Wiki v : val) {
                                slugList.add(v.getSlug());
                            }
                        },
                        error -> {
                            Log.e(TAG, error.getMessage());
                        },
                        () -> {
                            Log.d(TAG, "finish getAll");
                            ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                                    android.R.layout.simple_list_item_1, slugList);
                            wikiPageList.setAdapter(adapter);
                        },
                        d -> {
                            Log.d(TAG, "subscribe getAll");
                        }
                );
    }
}
