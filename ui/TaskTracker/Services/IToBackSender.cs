using System;
using System.Text.Json;
using System.Threading.Tasks;

namespace TaskTracker.Services;

public interface IToBackSender : IDisposable
{
    public Task<T?> GetData<T>(string path);

    public Task<T?> Post<T>(string path, T data);

    public static T? DeserializeResponse<T>(string responseBody) => JsonSerializer.Deserialize<T>(responseBody);
}