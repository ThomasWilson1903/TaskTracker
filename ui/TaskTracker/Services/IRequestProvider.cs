using System.Text.Json;
using System.Threading.Tasks;

namespace TaskTracker.Services;

public interface IRequestProvider
{
    public Task<T?> GetAsync<T>(string path, string token = "");

    public Task<TResult?> PostAsync<TRequest, TResult>(string path, TRequest data, string token = "");

    public Task DeleteAsync(string path, string token = "");

    public static string SerializeRequest<T>(T data) =>
        JsonSerializer.Serialize(data, new JsonSerializerOptions() { PropertyNamingPolicy = JsonNamingPolicy.SnakeCaseLower });

    public static T? DeserializeResponse<T>(string responseBody) => JsonSerializer.Deserialize<T>(responseBody);
}