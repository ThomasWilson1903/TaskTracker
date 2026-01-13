using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading;
using System.Threading.Tasks;

namespace TaskTracker.Services;

internal class RequestProvider : IRequestProvider
{
    private readonly string _server;
    private readonly Lazy<HttpClient> _httpClient = new Lazy<HttpClient>(() =>
    {
        var httpClient = new HttpClient();
        httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
        return httpClient;
    }, LazyThreadSafetyMode.ExecutionAndPublication);

    public RequestProvider()
    {
        _server = "http://localhost:8081";
    }

    public async Task<T?> GetAsync<T>(string path, string token = "")
    {
        using HttpResponseMessage response = await GetOrCreateHttpClient(token).GetAsync(new Uri(_server + path)).ConfigureAwait(false);
        response.EnsureSuccessStatusCode();
        string responseBody = await response.Content.ReadAsStringAsync().ConfigureAwait(false);

        var result = IRequestProvider.DeserializeResponse<T>(responseBody);
        if (result is null)
            throw new InvalidCastException();

        return result;
    }

    public async Task<TResult?> PostAsync<TRequest, TResult>(string path, TRequest data, string token = "")
    {
        using var content = new StringContent(IRequestProvider.SerializeRequest(data));

        if (content.Headers.ContentType is not null)
            content.Headers.ContentType.MediaType = "application/json";

        using HttpResponseMessage response = await GetOrCreateHttpClient(token).PostAsync(new Uri(_server + path), content).ConfigureAwait(false);
        response.EnsureSuccessStatusCode();

        var result = IRequestProvider.DeserializeResponse<TResult>(await response.Content.ReadAsStringAsync().ConfigureAwait(false));
        if (result is null)
            throw new InvalidCastException();

        return result;
    }

    public async Task DeleteAsync(string path, string token = "")
    {
        using HttpResponseMessage response = await GetOrCreateHttpClient(token).DeleteAsync(new Uri(_server + path)).ConfigureAwait(false);
        response.EnsureSuccessStatusCode();
    }

    private HttpClient GetOrCreateHttpClient(string token = "")
    {
        var httpClient = _httpClient.Value;

        if (!string.IsNullOrEmpty(token))
            httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", token);
        else
            httpClient.DefaultRequestHeaders.Authorization = null;

        return httpClient;
    }
}
