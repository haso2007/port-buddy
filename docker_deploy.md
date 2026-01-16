# PortBuddy 群晖 (Synology) NAS 部署指南

本文档记录了在群晖 NAS 上使用 Docker 成功部署 PortBuddy CLI 的实战配置，用于将本地 3000 端口服务公开至公网。

## 1. 环境准备

- **工作目录**：`/volume1/docker/portbuddy`
- **Token 存储**：`/volume1/docker/portbuddy/.port-buddy/token`
- **目标端口**：3000

### 配置文件设置
在群晖 **File Station** 中执行以下操作：
1. 在 `/docker/portbuddy` 下创建文件夹 `.port-buddy`。
2. 在 `.port-buddy` 内创建名为 `token` 的文件。
3. 将你的 Access Token 粘贴进文件并保存（确保无多余换行符）。

## 2. Docker Compose 配置 (已验证)

在群晖 **Container Manager** 中创建项目，使用以下经过验证的 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  portbuddy:
    image: portbuddy/portbuddy:latest
    container_name: portbuddy
    volumes:
      # 将本地包含 token 的目录挂载到容器内 root 家目录
      - /volume1/docker/portbuddy/.port-buddy:/root/.port-buddy:rw
    extra_hosts:
      # 映射宿主机网关，使容器能访问 NAS 宿主机的端口
      - "host.docker.internal:host-gateway"
    # 启动命令：反代 host.docker.internal 指向的宿主机 3000 端口
    command: host.docker.internal:3000
    restart: unless-stopped
```

## 3. 部署与验证

### 启动项目
使用 **Container Manager** 启动项目后，系统会自动创建网络并启动容器。

### 查看运行状态
点击容器 `portbuddy` 的 **日志** 选项卡，成功运行时应显示如下信息：

```text
Port Buddy - Mode: http

Local:  http://host.docker.internal:3000
Public: https://[你的随机子域名].portbuddy.dev

Press Ctrl+C to exit
----------------------------------------------

HTTP requests log:
(no requests yet)
```

## 4. 已知限制

### WebSocket / Socket.io 应用兼容性问题

经过实测，PortBuddy **不适合用于依赖 WebSocket 或 Socket.io 的应用**，例如：
- WebSSH2 (`billchurch/webssh2`)
- 实时协作工具
- 在线终端/IDE

**现象**：访问这类应用时，会出现 `Disconnected: Error: xhr poll error` 错误。

**原因**：PortBuddy 的 HTTP 网关在处理 WebSocket 升级握手和长连接保持方面存在限制，导致 Socket.io 无法正常建立持久连接。

**替代方案**：对于 WebSSH 等实时通信应用，建议使用 **Cloudflare Tunnel (`cloudflared`)**，它对 WebSocket 有完善的支持。

### 已验证可用的场景

以下场景经过实测，PortBuddy 可以正常工作：
- **群晖 DSM Web 界面** (端口 5000/50008 等)
- **普通 HTTP/HTTPS 网页服务**
- **REST API 服务**

### 适用场景对照表

| 使用场景 | PortBuddy | Cloudflare Tunnel |
|---------|-----------|-------------------|
| 群晖 DSM / 普通 HTTP 网页 | ✅ 已验证可用 | ✅ 可用 |
| REST API 服务 | ✅ 推荐 | ✅ 可用 |
| WebSSH / Socket.io 应用 | ❌ 不支持 | ✅ 推荐 |
| 数据库 / 原始 TCP 服务 | ✅ TCP 模式可用 | ✅ 可用 |

## 5. 常见问题说明

- **Public 地址访问失败**：请确保 NAS 宿主机上的 3000 端口服务已正常启动，且没有被群晖自带的防火墙拦截。
- **Token 错误**：如果日志提示认证失败，请检查 `/volume1/docker/portbuddy/.port-buddy/token` 文件内容。
- **域名固定**：PortBuddy 的免费版通常使用随机域名。如果需要固定域名，请参考官网文档在 `command` 中增加 `--domain=your-name` 参数。
