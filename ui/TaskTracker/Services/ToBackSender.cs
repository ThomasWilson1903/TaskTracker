using System;
using System.Net.Http;
using System.Text.Json;
using System.Threading.Tasks;

namespace TaskTracker.Services;

internal class ToBackSender : IToBackSender
{
    private readonly string _server;
    private readonly HttpClient _httpClient = new HttpClient();

    public ToBackSender(string server)
    {
        _server = server;
    }

    public async Task<T?> GetData<T>(string path)
    {
        using HttpResponseMessage response = await _httpClient.GetAsync(new Uri(_server + path)).ConfigureAwait(false);
        response.EnsureSuccessStatusCode();
        string responseBody = await response.Content.ReadAsStringAsync().ConfigureAwait(false);

        var result = IToBackSender.DeserializeResponse<T>(responseBody);
        if (result is null)
            throw new InvalidCastException();

        return result;
    }

    public async Task<T?> Post<T>(string path, T data)
    {
        var json = JsonSerializer.Serialize(data, new JsonSerializerOptions() { PropertyNamingPolicy = JsonNamingPolicy.SnakeCaseLower });
        using var content = new StringContent(json);

        if (content.Headers.ContentType is not null)
            content.Headers.ContentType.MediaType = "application/json";

        using HttpResponseMessage response = await _httpClient.PostAsync(new Uri(_server + path), content).ConfigureAwait(false);
        response.EnsureSuccessStatusCode();

        var result = IToBackSender.DeserializeResponse<T>(await response.Content.ReadAsStringAsync().ConfigureAwait(false));
        if (result is null)
            throw new InvalidCastException();

        return result;
    }

    public void Dispose()
    {
        if (_httpClient is not null)
            _httpClient.Dispose();
    }
}
