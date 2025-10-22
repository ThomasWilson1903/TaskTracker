using System.Collections.Generic;
using System.Threading.Tasks;

namespace TaskTracker.Services;

public interface IToBackSender
{
    public Task SendData<T>(string path, T data);
    
    public Task<IEnumerable<T>?> GetDatas<T>(string path);
}