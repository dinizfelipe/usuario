package com.felipe.usuario.business;


import com.felipe.usuario.business.converter.UsuarioConverter;
import com.felipe.usuario.business.dto.UsuarioDTO;
import com.felipe.usuario.infrastructure.entity.Usuario;
import com.felipe.usuario.infrastructure.exceptions.ConflictException;
import com.felipe.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.felipe.usuario.infrastructure.repository.UsuarioRepository;
import com.felipe.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    public void emailExiste(String email){
        try{
            boolean existe = verificaEmailExistente(email);
            if (existe){
                throw new ConflictException("Email já cadastrado:" + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email já cadastrado " + e.getCause());
        }
    }

    private boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscaUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                ()-> new ResourceNotFoundException("Email não encontrado " + email));
    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsaurio(String token, UsuarioDTO dto){
        String email = jwtUtil.extrarEmailToken(token.substring(7));
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não encontrado"));

        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }

}
